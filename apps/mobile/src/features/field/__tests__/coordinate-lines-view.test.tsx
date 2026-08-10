/* eslint-disable @typescript-eslint/no-require-imports */
import React from "react";
import TestRenderer, { act } from "react-test-renderer";

import { CoordinateLinesView } from "../coordinate-lines-view";

jest.mock("@eight2five/ui/components/hstack", () => {
  const ReactModule = require("react") as typeof React;
  const { View } = require("react-native") as typeof import("react-native");
  return {
    HStack: ({
      children,
      ...props
    }: React.PropsWithChildren<Record<string, unknown>>) =>
      ReactModule.createElement(View, props, children),
  };
});
jest.mock("@eight2five/ui/components/vstack", () => {
  const ReactModule = require("react") as typeof React;
  const { View } = require("react-native") as typeof import("react-native");
  return {
    VStack: ({
      children,
      ...props
    }: React.PropsWithChildren<Record<string, unknown>>) =>
      ReactModule.createElement(View, props, children),
  };
});
jest.mock("@eight2five/ui/components/text", () => {
  const ReactModule = require("react") as typeof React;
  const { Text } = require("react-native") as typeof import("react-native");
  return {
    Text: ({
      children,
      ...props
    }: React.PropsWithChildren<Record<string, unknown>>) =>
      ReactModule.createElement(Text, props, children),
  };
});
jest.mock("@eight2five/ui/components/icon", () => ({ Icon: () => null }));

describe("CoordinateLinesView", () => {
  test("keeps both coordinate rows single-line and allows them to shrink to fit", async () => {
    let renderer!: TestRenderer.ReactTestRenderer;
    await act(async () => {
      renderer = TestRenderer.create(
        <CoordinateLinesView
          coordinate={{
            side: "Side 1: 12.75 steps outside the 5-yard line",
            frontBack: "24.5 steps behind the back hash",
          }}
          color="#000"
          mutedColor="#666"
        />,
      );
    });

    const fittingText = renderer.root.findAll(
      (node) => node.props.adjustsFontSizeToFit === true,
    );
    expect(fittingText.length).toBeGreaterThanOrEqual(2);
    for (const node of fittingText) {
      expect(node.props.numberOfLines).toBe(1);
      expect(node.props.minimumFontScale).toBe(0.6);
      expect(node.props.style).toEqual(
        expect.objectContaining({ flexShrink: 1 }),
      );
    }

    await act(async () => renderer.unmount());
  });
});
