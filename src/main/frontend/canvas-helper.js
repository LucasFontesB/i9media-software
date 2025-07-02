import Chart from 'chart.js/auto';

const charts = {};

window.renderBarChart = (canvasId, titulo, labels, valores, legenda) => {
  const ctx = document.getElementById(canvasId)?.getContext('2d');
  if (!ctx) return;

  if (charts[canvasId]) {
    charts[canvasId].destroy();
  }

  charts[canvasId] = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: labels,
      datasets: [{
        label: legenda,
        data: valores,
        backgroundColor: '#4caf50'
      }]
    },
    options: {
      plugins: {
        title: {
          display: true,
          text: titulo
        },
        legend: {
          display: true
        }
      },
      responsive: true,
      maintainAspectRatio: false
    }
  });
};

window.renderLineChart = (canvasId, titulo, labels, valores, legenda) => {
  const ctx = document.getElementById(canvasId)?.getContext('2d');
  if (!ctx) return;

  if (charts[canvasId]) {
    charts[canvasId].destroy();
  }

  charts[canvasId] = new Chart(ctx, {
    type: 'line',
    data: {
      labels: labels,
      datasets: [{
        label: legenda,
        data: valores,
        borderColor: '#2196f3',
        tension: 0.3,
        fill: false
      }]
    },
    options: {
      plugins: {
        title: {
          display: true,
          text: titulo
        },
        legend: {
          display: true
        }
      },
      responsive: true,
      maintainAspectRatio: false
    }
  });
};

window.clearChart = (canvasId) => {
  if (charts[canvasId]) {
    charts[canvasId].destroy();
    delete charts[canvasId];
  }
};