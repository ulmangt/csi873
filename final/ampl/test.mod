model;

param n;

param a{1..n};

var x{1..n} >= 1;

minimize obj: sum { i in 1..n } x[i] * a[i];

option solver loqo;
