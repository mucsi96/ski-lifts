import { Component, computed, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { map, switchMap, startWith, catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { SkiResortService } from '../../services/ski-resort.service';
import { Lift, SkiResortDetail, LIFT_TYPE_ICONS, LiftType } from '../../models/ski-resort.model';
import { LiftCardComponent } from '../lift-card/lift-card.component';
import { ElevationChartComponent } from '../elevation-chart/elevation-chart.component';

interface ResourceState<T> {
  value: T | null;
  isLoading: boolean;
  error: Error | null;
}

@Component({
  selector: 'app-resort-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, LiftCardComponent, ElevationChartComponent],
  template: `
    <div class="resort-detail-page">
      <a routerLink="/" class="back-link">
        <span class="arrow">←</span> Back to all resorts
      </a>

      @if (resortState().isLoading) {
        <div class="loading-spinner"></div>
      } @else if (resortState().error) {
        <div class="error-message">
          Failed to load resort details. Please try again later.
        </div>
      } @else {
        @if (resort(); as r) {
          <div class="resort-header-card card">
            <div class="header-content">
              <div class="header-main">
                <h1>{{ r.name }}</h1>
                <p class="region">{{ r.region }}, {{ r.canton }}</p>
              </div>
              <div class="header-stats">
                <div class="stat">
                  <span class="stat-value">{{ formatDriveTime(r.driveTimeFromZurichMinutes) }}</span>
                  <span class="stat-label">from Zurich</span>
                </div>
                <div class="stat">
                  <span class="stat-value">{{ r.maxElevation }}m</span>
                  <span class="stat-label">max elevation</span>
                </div>
                <div class="stat">
                  <span class="stat-value">{{ r.totalSlopeKm }}km</span>
                  <span class="stat-label">total slopes</span>
                </div>
              </div>
            </div>

            <div class="slopes-detail">
              <h3>Slope Distribution</h3>
              <div class="slopes-bars">
                <div class="slope-bar">
                  <div class="bar-fill blue" [style.width.%]="getSlopePercentage(r.blueSlopes)"></div>
                  <span class="bar-label">Blue: {{ r.blueSlopes }}</span>
                </div>
                <div class="slope-bar">
                  <div class="bar-fill red" [style.width.%]="getSlopePercentage(r.redSlopes)"></div>
                  <span class="bar-label">Red: {{ r.redSlopes }}</span>
                </div>
                <div class="slope-bar">
                  <div class="bar-fill black" [style.width.%]="getSlopePercentage(r.blackSlopes)"></div>
                  <span class="bar-label">Black: {{ r.blackSlopes }}</span>
                </div>
              </div>
            </div>

            @if (r.websiteUrl) {
              <a [href]="r.websiteUrl" target="_blank" class="btn btn-primary website-link">
                Visit Website
              </a>
            }
          </div>

          <section class="lifts-section">
            <div class="section-header">
              <h2>Lifts ({{ r.lifts.length }})</h2>
              <div class="lift-type-filter">
                <button
                  class="filter-btn"
                  [class.active]="selectedLiftType() === null"
                  (click)="selectedLiftType.set(null)"
                >
                  All
                </button>
                @for (type of liftTypes; track type) {
                  <button
                    class="filter-btn"
                    [class.active]="selectedLiftType() === type"
                    (click)="selectedLiftType.set(type)"
                  >
                    {{ getLiftIcon(type) }} {{ type.replace('_', ' ') }}
                  </button>
                }
              </div>
            </div>

            <div class="lifts-grid">
              @for (lift of filteredLifts(); track lift.id) {
                <app-lift-card
                  [lift]="lift"
                  [isExpanded]="expandedLiftId() === lift.id"
                  (toggle)="toggleLift(lift.id)"
                />
              }
            </div>
          </section>

          @if (selectedLift(); as lift) {
            <section class="elevation-section">
              <h2>Elevation Profile: {{ lift.name }}</h2>
              <app-elevation-chart [lift]="lift" />
            </section>
          }
        }
      }
    </div>
  `,
  styles: [`
    .resort-detail-page {
      padding-bottom: 2rem;
    }

    .back-link {
      display: inline-flex;
      align-items: center;
      gap: 0.5rem;
      color: var(--primary-color);
      text-decoration: none;
      margin-bottom: 1.5rem;
      font-weight: 500;

      &:hover {
        text-decoration: underline;
      }

      .arrow {
        font-size: 1.25rem;
      }
    }

    .resort-header-card {
      padding: 2rem;
      margin-bottom: 2rem;
    }

    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 2rem;
      flex-wrap: wrap;
      gap: 1.5rem;
    }

    .header-main {
      h1 {
        font-size: 2rem;
        font-weight: 700;
        margin-bottom: 0.5rem;
      }

      .region {
        color: var(--text-secondary);
        font-size: 1.125rem;
      }
    }

    .header-stats {
      display: flex;
      gap: 2rem;
    }

    .stat {
      text-align: center;

      .stat-value {
        display: block;
        font-size: 1.5rem;
        font-weight: 700;
        color: var(--primary-color);
      }

      .stat-label {
        font-size: 0.75rem;
        color: var(--text-secondary);
        text-transform: uppercase;
      }
    }

    .slopes-detail {
      margin-bottom: 1.5rem;

      h3 {
        font-size: 1rem;
        margin-bottom: 1rem;
        color: var(--text-secondary);
      }
    }

    .slopes-bars {
      display: flex;
      flex-direction: column;
      gap: 0.75rem;
    }

    .slope-bar {
      position: relative;
      height: 32px;
      background: var(--background-color);
      border-radius: 8px;
      overflow: hidden;

      .bar-fill {
        height: 100%;
        border-radius: 8px;
        transition: width 0.3s ease;

        &.blue { background: var(--blue-slope); }
        &.red { background: var(--red-slope); }
        &.black { background: var(--black-slope); }
      }

      .bar-label {
        position: absolute;
        left: 1rem;
        top: 50%;
        transform: translateY(-50%);
        font-weight: 500;
        font-size: 0.875rem;
        color: white;
        text-shadow: 0 1px 2px rgba(0,0,0,0.3);
      }
    }

    .website-link {
      display: inline-block;
    }

    .lifts-section {
      margin-bottom: 2rem;
    }

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1.5rem;
      flex-wrap: wrap;
      gap: 1rem;

      h2 {
        font-size: 1.5rem;
        font-weight: 600;
      }
    }

    .lift-type-filter {
      display: flex;
      gap: 0.5rem;
      flex-wrap: wrap;
    }

    .filter-btn {
      padding: 0.5rem 1rem;
      border: 1px solid var(--border-color);
      border-radius: 20px;
      background: white;
      cursor: pointer;
      font-size: 0.75rem;
      transition: all 0.2s;

      &:hover {
        border-color: var(--primary-color);
      }

      &.active {
        background: var(--primary-color);
        color: white;
        border-color: var(--primary-color);
      }
    }

    .lifts-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
      gap: 1rem;
    }

    .elevation-section {
      margin-top: 2rem;

      h2 {
        font-size: 1.25rem;
        margin-bottom: 1rem;
      }
    }

    .error-message {
      text-align: center;
      padding: 2rem;
      color: var(--danger-color);
      background: rgba(239, 68, 68, 0.1);
      border-radius: 8px;
    }
  `]
})
export class ResortDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly skiResortService = inject(SkiResortService);

  selectedLiftType = signal<LiftType | null>(null);
  expandedLiftId = signal<number | null>(null);

  liftTypes: LiftType[] = ['GONDOLA', 'CABLE_CAR', 'CHAIRLIFT', 'DRAG_LIFT', 'FUNICULAR'];

  private resortResource$ = this.route.paramMap.pipe(
    map((params) => Number(params.get('id'))),
    switchMap((id: number) =>
      this.skiResortService.getResortById(id).pipe(
        map((resort): ResourceState<SkiResortDetail> => ({
          value: resort,
          isLoading: false,
          error: null
        })),
        startWith<ResourceState<SkiResortDetail>>({
          value: null,
          isLoading: true,
          error: null
        }),
        catchError((err: Error) => of<ResourceState<SkiResortDetail>>({
          value: null,
          isLoading: false,
          error: err
        }))
      )
    )
  );

  resortState = toSignal(this.resortResource$, {
    initialValue: { value: null, isLoading: true, error: null } as ResourceState<SkiResortDetail>
  });

  resort = computed(() => this.resortState()?.value);

  filteredLifts = computed(() => {
    const r = this.resort();
    if (!r) return [];
    const type = this.selectedLiftType();
    if (!type) return r.lifts;
    return r.lifts.filter((lift: Lift) => lift.liftType === type);
  });

  selectedLift = computed(() => {
    const r = this.resort();
    const liftId = this.expandedLiftId();
    if (!r || !liftId) return null;
    return r.lifts.find((l: Lift) => l.id === liftId) ?? null;
  });

  toggleLift(liftId: number): void {
    this.expandedLiftId.update((current) => (current === liftId ? null : liftId));
  }

  formatDriveTime(minutes: number): string {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    if (hours === 0) return `${mins} min`;
    if (mins === 0) return `${hours}h`;
    return `${hours}h ${mins}min`;
  }

  getSlopePercentage(count: number): number {
    const r = this.resort();
    if (!r) return 0;
    const total = r.blueSlopes + r.redSlopes + r.blackSlopes;
    return total > 0 ? (count / total) * 100 : 0;
  }

  getLiftIcon(type: LiftType): string {
    return LIFT_TYPE_ICONS[type] ?? '';
  }
}
