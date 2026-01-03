import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Lift, LIFT_TYPE_ICONS } from '../../models/ski-resort.model';

@Component({
  selector: 'app-lift-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="lift-card card" [class.expanded]="isExpanded" (click)="toggle.emit()">
      <div class="lift-header">
        <div class="lift-icon">{{ getLiftIcon() }}</div>
        <div class="lift-info">
          <h4>{{ lift.name }}</h4>
          <span class="lift-type">{{ lift.liftTypeDisplayName }}</span>
        </div>
        <div class="expand-icon" [class.rotated]="isExpanded">
          ▼
        </div>
      </div>

      <div class="lift-stats">
        <div class="stat">
          <span class="stat-label">Start</span>
          <span class="stat-value">{{ lift.startElevation }}m</span>
        </div>
        <div class="stat elevation-arrow">
          <span class="arrow">→</span>
          <span class="gain">+{{ lift.elevationGain }}m</span>
        </div>
        <div class="stat">
          <span class="stat-label">End</span>
          <span class="stat-value">{{ lift.endElevation }}m</span>
        </div>
      </div>

      @if (isExpanded) {
        <div class="lift-details">
          <div class="detail-row">
            <span class="detail-label">Length:</span>
            <span class="detail-value">{{ formatLength(lift.lengthMeters) }}</span>
          </div>
          @if (lift.capacityPerHour) {
            <div class="detail-row">
              <span class="detail-label">Capacity:</span>
              <span class="detail-value">{{ lift.capacityPerHour.toLocaleString() }}/hour</span>
            </div>
          }
          <div class="detail-row">
            <span class="detail-label">Coordinates:</span>
            <span class="detail-value coordinates">
              {{ lift.startLatitude?.toFixed(4) }}, {{ lift.startLongitude?.toFixed(4) }}
            </span>
          </div>
          @if (lift.elevationProfile && lift.elevationProfile.length > 0) {
            <div class="profile-indicator">
              <span class="checkmark">✓</span> Elevation profile available ({{ lift.elevationProfile.length }} points)
            </div>
          } @else {
            <div class="profile-indicator missing">
              No elevation profile data
            </div>
          }
        </div>
      }
    </div>
  `,
  styles: [`
    .lift-card {
      padding: 1.25rem;
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover {
        border-color: var(--primary-color);
      }

      &.expanded {
        border-color: var(--primary-color);
        box-shadow: 0 4px 12px rgba(37, 99, 235, 0.15);
      }
    }

    .lift-header {
      display: flex;
      align-items: center;
      gap: 1rem;
      margin-bottom: 1rem;
    }

    .lift-icon {
      font-size: 1.5rem;
      width: 48px;
      height: 48px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--background-color);
      border-radius: 12px;
    }

    .lift-info {
      flex: 1;

      h4 {
        font-size: 1rem;
        font-weight: 600;
        margin-bottom: 0.25rem;
      }

      .lift-type {
        font-size: 0.75rem;
        color: var(--text-secondary);
      }
    }

    .expand-icon {
      color: var(--text-secondary);
      font-size: 0.75rem;
      transition: transform 0.2s ease;

      &.rotated {
        transform: rotate(180deg);
      }
    }

    .lift-stats {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0.75rem;
      background: var(--background-color);
      border-radius: 8px;
    }

    .stat {
      text-align: center;

      .stat-label {
        display: block;
        font-size: 0.625rem;
        color: var(--text-secondary);
        text-transform: uppercase;
        margin-bottom: 0.25rem;
      }

      .stat-value {
        font-weight: 600;
        font-size: 1rem;
      }

      &.elevation-arrow {
        .arrow {
          display: block;
          color: var(--text-secondary);
        }

        .gain {
          font-weight: 600;
          color: var(--success-color);
          font-size: 0.875rem;
        }
      }
    }

    .lift-details {
      margin-top: 1rem;
      padding-top: 1rem;
      border-top: 1px solid var(--border-color);
    }

    .detail-row {
      display: flex;
      justify-content: space-between;
      padding: 0.5rem 0;

      .detail-label {
        color: var(--text-secondary);
        font-size: 0.875rem;
      }

      .detail-value {
        font-weight: 500;
        font-size: 0.875rem;

        &.coordinates {
          font-family: monospace;
          font-size: 0.75rem;
        }
      }
    }

    .profile-indicator {
      margin-top: 0.75rem;
      padding: 0.5rem 0.75rem;
      background: rgba(34, 197, 94, 0.1);
      color: var(--success-color);
      border-radius: 6px;
      font-size: 0.75rem;
      font-weight: 500;

      .checkmark {
        margin-right: 0.5rem;
      }

      &.missing {
        background: rgba(107, 114, 128, 0.1);
        color: var(--text-secondary);
      }
    }
  `]
})
export class LiftCardComponent {
  @Input({ required: true }) lift!: Lift;
  @Input() isExpanded = false;
  @Output() toggle = new EventEmitter<void>();

  getLiftIcon(): string {
    return LIFT_TYPE_ICONS[this.lift.liftType] ?? '🎿';
  }

  formatLength(meters: number): string {
    if (meters >= 1000) {
      return `${(meters / 1000).toFixed(1)} km`;
    }
    return `${meters} m`;
  }
}
