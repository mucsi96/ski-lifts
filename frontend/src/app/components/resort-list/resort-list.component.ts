import { Component, computed, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { rxResource } from '@angular/core/rxjs-interop';
import { SkiResortService } from '../../services/ski-resort.service';
import { SkiResort } from '../../models/ski-resort.model';

@Component({
  selector: 'app-resort-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <div class="resort-list-page">
      <div class="filters">
        <div class="search-box">
          <input
            type="text"
            placeholder="Search resorts..."
            [ngModel]="searchQuery()"
            (ngModelChange)="searchQuery.set($event)"
            class="search-input"
          />
        </div>
        <div class="filter-group">
          <label>Max drive time from Zurich:</label>
          <select
            [ngModel]="maxDriveTime()"
            (ngModelChange)="maxDriveTime.set($event)"
            class="filter-select"
          >
            <option [ngValue]="null">All</option>
            <option [ngValue]="60">1 hour</option>
            <option [ngValue]="90">1.5 hours</option>
            <option [ngValue]="120">2 hours</option>
            <option [ngValue]="180">3 hours</option>
          </select>
        </div>
        <div class="filter-group">
          <label>Sort by:</label>
          <select
            [ngModel]="sortBy()"
            (ngModelChange)="sortBy.set($event)"
            class="filter-select"
          >
            <option value="name">Name</option>
            <option value="driveTime">Drive Time</option>
            <option value="slopes">Total Slopes</option>
            <option value="elevation">Max Elevation</option>
          </select>
        </div>
      </div>

      @if (resortsResource.isLoading()) {
        <div class="loading-spinner"></div>
      } @else if (resortsResource.error()) {
        <div class="error-message">
          Failed to load resorts. Please try again later.
        </div>
      } @else {
        <div class="results-info">
          Found {{ filteredResorts().length }} ski resorts
        </div>
        <div class="resort-grid">
          @for (resort of filteredResorts(); track resort.id) {
            <a [routerLink]="['/resort', resort.id]" class="resort-card card">
              <div class="resort-header">
                <h3>{{ resort.name }}</h3>
                <span class="region-badge">{{ resort.canton }}</span>
              </div>
              <div class="resort-info">
                <div class="info-row">
                  <span class="label">Region:</span>
                  <span class="value">{{ resort.region }}</span>
                </div>
                <div class="info-row">
                  <span class="label">Drive from Zurich:</span>
                  <span class="value">{{ formatDriveTime(resort.driveTimeFromZurichMinutes) }}</span>
                </div>
                <div class="info-row">
                  <span class="label">Elevation:</span>
                  <span class="value">{{ resort.minElevation }}m - {{ resort.maxElevation }}m</span>
                </div>
                <div class="info-row">
                  <span class="label">Slopes (km):</span>
                  <span class="value">{{ resort.totalSlopeKm }} km</span>
                </div>
              </div>
              <div class="slopes-summary">
                <span class="badge badge-blue">{{ resort.blueSlopes }} Blue</span>
                <span class="badge badge-red">{{ resort.redSlopes }} Red</span>
                <span class="badge badge-black">{{ resort.blackSlopes }} Black</span>
              </div>
              <div class="lifts-count">
                {{ resort.liftCount }} lifts
              </div>
            </a>
          }
        </div>
      }
    </div>
  `,
  styles: [`
    .resort-list-page {
      padding-bottom: 2rem;
    }

    .filters {
      display: flex;
      gap: 1rem;
      margin-bottom: 1.5rem;
      flex-wrap: wrap;
      align-items: flex-end;
    }

    .search-box {
      flex: 1;
      min-width: 250px;
    }

    .search-input {
      width: 100%;
      padding: 0.75rem 1rem;
      border: 1px solid var(--border-color);
      border-radius: 8px;
      font-size: 1rem;
      outline: none;
      transition: border-color 0.2s;

      &:focus {
        border-color: var(--primary-color);
      }
    }

    .filter-group {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;

      label {
        font-size: 0.875rem;
        color: var(--text-secondary);
      }
    }

    .filter-select {
      padding: 0.75rem 1rem;
      border: 1px solid var(--border-color);
      border-radius: 8px;
      font-size: 0.875rem;
      background: white;
      cursor: pointer;
      min-width: 150px;
    }

    .results-info {
      color: var(--text-secondary);
      margin-bottom: 1rem;
      font-size: 0.875rem;
    }

    .resort-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
      gap: 1.5rem;
    }

    .resort-card {
      display: block;
      padding: 1.5rem;
      text-decoration: none;
      color: inherit;
      cursor: pointer;
    }

    .resort-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 1rem;

      h3 {
        font-size: 1.25rem;
        font-weight: 600;
        color: var(--text-primary);
      }
    }

    .region-badge {
      background: var(--background-color);
      padding: 0.25rem 0.5rem;
      border-radius: 4px;
      font-size: 0.75rem;
      font-weight: 500;
      color: var(--text-secondary);
    }

    .resort-info {
      margin-bottom: 1rem;
    }

    .info-row {
      display: flex;
      justify-content: space-between;
      padding: 0.375rem 0;
      border-bottom: 1px solid var(--border-color);

      &:last-child {
        border-bottom: none;
      }

      .label {
        color: var(--text-secondary);
        font-size: 0.875rem;
      }

      .value {
        font-weight: 500;
        font-size: 0.875rem;
      }
    }

    .slopes-summary {
      display: flex;
      gap: 0.5rem;
      margin-bottom: 1rem;
    }

    .lifts-count {
      text-align: center;
      padding: 0.5rem;
      background: var(--background-color);
      border-radius: 8px;
      font-weight: 500;
      font-size: 0.875rem;
      color: var(--primary-color);
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
export class ResortListComponent {
  private readonly skiResortService = inject(SkiResortService);

  searchQuery = signal('');
  maxDriveTime = signal<number | null>(null);
  sortBy = signal<'name' | 'driveTime' | 'slopes' | 'elevation'>('name');

  resortsResource = rxResource({
    loader: () => this.skiResortService.getAllResorts()
  });

  filteredResorts = computed(() => {
    const resorts = this.resortsResource.value() ?? [];
    const query = this.searchQuery().toLowerCase();
    const maxTime = this.maxDriveTime();
    const sort = this.sortBy();

    const filtered = resorts.filter((resort: SkiResort) => {
      const matchesSearch =
        !query ||
        resort.name.toLowerCase().includes(query) ||
        resort.region.toLowerCase().includes(query);
      const matchesDriveTime =
        !maxTime || resort.driveTimeFromZurichMinutes <= maxTime;
      return matchesSearch && matchesDriveTime;
    });

    return filtered.sort((a: SkiResort, b: SkiResort) => {
      switch (sort) {
        case 'name':
          return a.name.localeCompare(b.name);
        case 'driveTime':
          return a.driveTimeFromZurichMinutes - b.driveTimeFromZurichMinutes;
        case 'slopes':
          return (
            b.blueSlopes +
            b.redSlopes +
            b.blackSlopes -
            (a.blueSlopes + a.redSlopes + a.blackSlopes)
          );
        case 'elevation':
          return b.maxElevation - a.maxElevation;
        default:
          return 0;
      }
    });
  });

  formatDriveTime(minutes: number): string {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    if (hours === 0) return `${mins} min`;
    if (mins === 0) return `${hours}h`;
    return `${hours}h ${mins}min`;
  }
}
