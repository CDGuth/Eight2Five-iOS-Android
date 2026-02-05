import React from "react";
import { View, Text, TouchableOpacity, Alert } from "react-native";
import { styles } from "../styles";

export const LabelWithTooltip = ({
  label,
  tooltip,
}: {
  label: string;
  tooltip?: string;
}) => (
  <View style={styles.labelContainer}>
    <Text style={styles.labelText}>{label}</Text>
    {tooltip && (
      <TouchableOpacity
        onPress={() => Alert.alert(label, tooltip)}
        style={{ marginLeft: 6 }}
      >
        <View
          style={{
            width: 16,
            height: 16,
            borderRadius: 8,
            backgroundColor: "#eee",
            alignItems: "center",
            justifyContent: "center",
            borderWidth: 1,
            borderColor: "#ddd",
          }}
        >
          <Text style={{ fontSize: 10, fontWeight: "bold", color: "#888" }}>
            ?
          </Text>
        </View>
      </TouchableOpacity>
    )}
  </View>
);
