export interface SkiResort {
  id: number;
  name: string;
  region: string;
  canton: string;
  driveTimeFromZurichMinutes: number;
  minElevation: number;
  maxElevation: number;
  blueSlopes: number;
  redSlopes: number;
  blackSlopes: number;
  totalSlopeKm: number;
  latitude: number;
  longitude: number;
  websiteUrl: string;
  liftCount: number;
}

export interface SkiResortDetail extends SkiResort {
  lifts: Lift[];
}

export interface Lift {
  id: number;
  name: string;
  liftType: LiftType;
  liftTypeDisplayName: string;
  startElevation: number;
  endElevation: number;
  elevationGain: number;
  lengthMeters: number;
  capacityPerHour: number;
  startLatitude: number;
  startLongitude: number;
  endLatitude: number;
  endLongitude: number;
  elevationProfile: ElevationPoint[];
}

export interface ElevationPoint {
  latitude: number;
  longitude: number;
  elevation: number;
  distanceFromStart: number;
}

export type LiftType =
  | 'CHAIRLIFT'
  | 'GONDOLA'
  | 'CABLE_CAR'
  | 'DRAG_LIFT'
  | 'T_BAR'
  | 'MAGIC_CARPET'
  | 'FUNICULAR';

export const LIFT_TYPE_ICONS: Record<LiftType, string> = {
  CHAIRLIFT: '🪑',
  GONDOLA: '🚡',
  CABLE_CAR: '🚠',
  DRAG_LIFT: '⬆️',
  T_BAR: '🎿',
  MAGIC_CARPET: '➡️',
  FUNICULAR: '🚃'
};
