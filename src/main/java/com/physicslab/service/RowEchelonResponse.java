package com.physicslab.service;

import java.util.List;

public record RowEchelonResponse(
    double[][] echelonForm,
    int rank,
    List<Integer> pivotColumns) {
}
