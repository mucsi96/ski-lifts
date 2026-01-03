import { Component, Input, computed, signal, ElementRef, ViewChild, AfterViewInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Lift, ElevationPoint } from '../../models/ski-resort.model';

@Component({
  selector: 'app-elevation-chart',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="elevation-chart card">
      @if (!hasElevationData()) {
        <div class="no-data">
          <p>No elevation profile data available for this lift.</p>
          <p class="hint">Elevation data is fetched from the Open Elevation API based on lift coordinates.</p>
        </div>
      } @else {
        <div class="chart-header">
          <div class="chart-stat">
            <span class="label">Start Elevation</span>
            <span class="value">{{ lift.startElevation }}m</span>
          </div>
          <div class="chart-stat">
            <span class="label">End Elevation</span>
            <span class="value">{{ lift.endElevation }}m</span>
          </div>
          <div class="chart-stat">
            <span class="label">Elevation Gain</span>
            <span class="value gain">+{{ lift.elevationGain }}m</span>
          </div>
          <div class="chart-stat">
            <span class="label">Total Length</span>
            <span class="value">{{ formatLength(lift.lengthMeters) }}</span>
          </div>
        </div>

        <div class="chart-container" #chartContainer>
          <svg
            [attr.viewBox]="viewBox()"
            class="elevation-svg"
            preserveAspectRatio="xMidYMid meet"
          >
            <!-- Grid lines -->
            @for (line of gridLines(); track line.y) {
              <line
                [attr.x1]="padding"
                [attr.y1]="line.y"
                [attr.x2]="chartWidth() - padding"
                [attr.y2]="line.y"
                class="grid-line"
              />
              <text
                [attr.x]="padding - 10"
                [attr.y]="line.y + 4"
                class="axis-label"
                text-anchor="end"
              >
                {{ line.elevation }}m
              </text>
            }

            <!-- Gradient definition -->
            <defs>
              <linearGradient id="elevationGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                <stop offset="0%" stop-color="#3b82f6" stop-opacity="0.4"/>
                <stop offset="100%" stop-color="#3b82f6" stop-opacity="0.05"/>
              </linearGradient>
            </defs>

            <!-- Area fill -->
            <path
              [attr.d]="areaPath()"
              fill="url(#elevationGradient)"
            />

            <!-- Elevation line -->
            <path
              [attr.d]="linePath()"
              class="elevation-line"
              fill="none"
            />

            <!-- Data points -->
            @for (point of chartPoints(); track $index) {
              <circle
                [attr.cx]="point.x"
                [attr.cy]="point.y"
                r="4"
                class="data-point"
                (mouseenter)="hoveredPoint.set($index)"
                (mouseleave)="hoveredPoint.set(null)"
              />
            }

            <!-- Tooltip -->
            @if (hoveredPointData(); as hovered) {
              <g class="tooltip-group">
                <rect
                  [attr.x]="hovered.x - 45"
                  [attr.y]="hovered.y - 50"
                  width="90"
                  height="40"
                  rx="4"
                  class="tooltip-bg"
                />
                <text
                  [attr.x]="hovered.x"
                  [attr.y]="hovered.y - 35"
                  text-anchor="middle"
                  class="tooltip-text"
                >
                  {{ hovered.elevation }}m
                </text>
                <text
                  [attr.x]="hovered.x"
                  [attr.y]="hovered.y - 20"
                  text-anchor="middle"
                  class="tooltip-subtext"
                >
                  {{ formatDistance(hovered.distance) }}
                </text>
              </g>
            }

            <!-- X-axis labels -->
            @for (label of xAxisLabels(); track label.distance) {
              <text
                [attr.x]="label.x"
                [attr.y]="chartHeight() - padding + 20"
                text-anchor="middle"
                class="axis-label"
              >
                {{ formatDistance(label.distance) }}
              </text>
            }
          </svg>
        </div>

        <div class="chart-legend">
          <div class="legend-item">
            <span class="legend-line"></span>
            <span>Elevation Profile</span>
          </div>
          <span class="legend-info">
            Based on {{ lift.elevationProfile.length }} measurement points
          </span>
        </div>
      }
    </div>
  `,
  styles: [`
    .elevation-chart {
      padding: 1.5rem;
    }

    .no-data {
      text-align: center;
      padding: 2rem;
      color: var(--text-secondary);

      p {
        margin-bottom: 0.5rem;
      }

      .hint {
        font-size: 0.875rem;
        opacity: 0.7;
      }
    }

    .chart-header {
      display: flex;
      justify-content: space-around;
      margin-bottom: 1.5rem;
      flex-wrap: wrap;
      gap: 1rem;
    }

    .chart-stat {
      text-align: center;

      .label {
        display: block;
        font-size: 0.75rem;
        color: var(--text-secondary);
        margin-bottom: 0.25rem;
      }

      .value {
        font-size: 1.25rem;
        font-weight: 600;

        &.gain {
          color: var(--success-color);
        }
      }
    }

    .chart-container {
      width: 100%;
      height: 300px;
      background: var(--background-color);
      border-radius: 8px;
      overflow: hidden;
    }

    .elevation-svg {
      width: 100%;
      height: 100%;
    }

    .grid-line {
      stroke: var(--border-color);
      stroke-width: 1;
      stroke-dasharray: 4, 4;
    }

    .axis-label {
      font-size: 10px;
      fill: var(--text-secondary);
    }

    .elevation-line {
      stroke: var(--primary-color);
      stroke-width: 3;
      stroke-linecap: round;
      stroke-linejoin: round;
    }

    .data-point {
      fill: white;
      stroke: var(--primary-color);
      stroke-width: 2;
      cursor: pointer;
      transition: r 0.2s ease;

      &:hover {
        r: 6;
      }
    }

    .tooltip-bg {
      fill: var(--text-primary);
    }

    .tooltip-text {
      fill: white;
      font-size: 12px;
      font-weight: 600;
    }

    .tooltip-subtext {
      fill: rgba(255, 255, 255, 0.7);
      font-size: 10px;
    }

    .chart-legend {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 1rem;
      padding-top: 1rem;
      border-top: 1px solid var(--border-color);
    }

    .legend-item {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      font-size: 0.875rem;
    }

    .legend-line {
      width: 24px;
      height: 3px;
      background: var(--primary-color);
      border-radius: 2px;
    }

    .legend-info {
      font-size: 0.75rem;
      color: var(--text-secondary);
    }
  `]
})
export class ElevationChartComponent implements OnChanges {
  @Input({ required: true }) lift!: Lift;

  readonly padding = 60;
  readonly chartWidth = signal(600);
  readonly chartHeight = signal(300);

  hoveredPoint = signal<number | null>(null);

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['lift']) {
      this.hoveredPoint.set(null);
    }
  }

  hasElevationData = computed(() => {
    return this.lift?.elevationProfile && this.lift.elevationProfile.length > 0;
  });

  viewBox = computed(() => {
    return `0 0 ${this.chartWidth()} ${this.chartHeight()}`;
  });

  private elevationRange = computed(() => {
    if (!this.hasElevationData()) return { min: 0, max: 0 };
    const elevations = this.lift.elevationProfile.map(p => p.elevation);
    const min = Math.min(...elevations);
    const max = Math.max(...elevations);
    const range = max - min;
    return {
      min: min - range * 0.1,
      max: max + range * 0.1
    };
  });

  private distanceRange = computed(() => {
    if (!this.hasElevationData()) return { max: 0 };
    const distances = this.lift.elevationProfile.map(p => p.distanceFromStart);
    return { max: Math.max(...distances) };
  });

  chartPoints = computed(() => {
    if (!this.hasElevationData()) return [];

    const { min: minElev, max: maxElev } = this.elevationRange();
    const { max: maxDist } = this.distanceRange();
    const width = this.chartWidth();
    const height = this.chartHeight();
    const pad = this.padding;

    const xScale = (d: number) => pad + (d / maxDist) * (width - 2 * pad);
    const yScale = (e: number) => height - pad - ((e - minElev) / (maxElev - minElev)) * (height - 2 * pad);

    return this.lift.elevationProfile.map((point) => ({
      x: xScale(point.distanceFromStart),
      y: yScale(point.elevation),
      elevation: Math.round(point.elevation),
      distance: point.distanceFromStart
    }));
  });

  linePath = computed(() => {
    const points = this.chartPoints();
    if (points.length === 0) return '';

    return points.map((p, i) =>
      i === 0 ? `M ${p.x} ${p.y}` : `L ${p.x} ${p.y}`
    ).join(' ');
  });

  areaPath = computed(() => {
    const points = this.chartPoints();
    if (points.length === 0) return '';

    const height = this.chartHeight();
    const pad = this.padding;
    const bottomY = height - pad;

    const linePart = points.map((p, i) =>
      i === 0 ? `M ${p.x} ${p.y}` : `L ${p.x} ${p.y}`
    ).join(' ');

    const lastPoint = points[points.length - 1];
    const firstPoint = points[0];

    return `${linePart} L ${lastPoint.x} ${bottomY} L ${firstPoint.x} ${bottomY} Z`;
  });

  gridLines = computed(() => {
    if (!this.hasElevationData()) return [];

    const { min, max } = this.elevationRange();
    const height = this.chartHeight();
    const pad = this.padding;
    const range = max - min;

    const step = Math.ceil(range / 5 / 50) * 50;
    const lines: Array<{ y: number; elevation: number }> = [];

    for (let elev = Math.ceil(min / step) * step; elev <= max; elev += step) {
      const y = height - pad - ((elev - min) / range) * (height - 2 * pad);
      lines.push({ y, elevation: Math.round(elev) });
    }

    return lines;
  });

  xAxisLabels = computed(() => {
    const points = this.chartPoints();
    if (points.length === 0) return [];

    const numLabels = 5;
    const step = Math.floor(points.length / (numLabels - 1));

    const labels: Array<{ x: number; distance: number }> = [];
    for (let i = 0; i < points.length; i += step) {
      labels.push({
        x: points[i].x,
        distance: points[i].distance
      });
    }

    // Always include last point
    if (labels[labels.length - 1]?.distance !== points[points.length - 1].distance) {
      labels.push({
        x: points[points.length - 1].x,
        distance: points[points.length - 1].distance
      });
    }

    return labels;
  });

  hoveredPointData = computed(() => {
    const idx = this.hoveredPoint();
    if (idx === null) return null;
    const points = this.chartPoints();
    return points[idx] ?? null;
  });

  formatLength(meters: number): string {
    if (meters >= 1000) {
      return `${(meters / 1000).toFixed(1)} km`;
    }
    return `${meters} m`;
  }

  formatDistance(distance: number): string {
    if (distance >= 1000) {
      return `${(distance / 1000).toFixed(1)}km`;
    }
    return `${Math.round(distance)}m`;
  }
}
