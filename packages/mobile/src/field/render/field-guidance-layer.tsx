import { DashPathEffect, Line } from "@shopify/react-native-skia";
import { useDerivedValue, type SharedValue } from "react-native-reanimated";

import type { FieldPoint } from "../types";
import { STANDARD_STEP_METERS } from "../units";

const GUIDANCE_DASH_LENGTH_METERS = STANDARD_STEP_METERS;
const GUIDANCE_DASH_GAP_METERS = STANDARD_STEP_METERS * 0.625;

export function FieldGuidanceLayer({
  livePosition,
  targetPosition,
  metersPerPixel,
  color,
}: {
  readonly livePosition: SharedValue<FieldPoint | null>;
  readonly targetPosition: FieldPoint;
  readonly metersPerPixel: SharedValue<number>;
  readonly color: string;
}) {
  const livePoint = useDerivedValue(() => {
    const position = livePosition.value ?? targetPosition;
    return { x: position.xMeters, y: position.yMeters };
  }, [targetPosition.xMeters, targetPosition.yMeters]);
  const targetPoint = {
    x: targetPosition.xMeters,
    y: targetPosition.yMeters,
  };
  const opacity = useDerivedValue(() =>
    livePosition.value === null ? 0 : 0.82,
  );
  const strokeWidth = useDerivedValue(() => metersPerPixel.value * 2.4);
  // World-space dash intervals keep dash count tied only to physical distance;
  // zoom changes their apparent size, not how many fit along the connector.
  const dashIntervals = [GUIDANCE_DASH_LENGTH_METERS, GUIDANCE_DASH_GAP_METERS];
  return (
    <Line
      p1={livePoint}
      p2={targetPoint}
      color={color}
      opacity={opacity}
      strokeWidth={strokeWidth}
      strokeCap="round"
    >
      <DashPathEffect intervals={dashIntervals} />
    </Line>
  );
}
