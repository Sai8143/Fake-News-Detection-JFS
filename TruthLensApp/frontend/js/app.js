// TruthLens Spring Boot Full Stack SPA Controller

let currentProfile = {
  name: 'News Reader',
  interests: ['technology', 'science', 'health'],
  apiKey: '',
  scanned: 0,
  fakeDetected: 0
};

document.addEventListener('DOMContentLoaded', () => {
  initTabs();
  checkHealth();
  loadProfile();
  loadFeed('technology');
  loadHistory();
  setupEventListeners();
});

// Tab Navigation
function initTabs() {
  const tabs = document.querySelectorAll('.nav-tab-btn');
  tabs.forEach(tab => {
    tab.addEventListener('click', () => {
      const targetId = tab.dataset.tab;
      document.querySelectorAll('.nav-tab-btn').forEach(t => t.classList.remove('active'));
      document.querySelectorAll('.tab-pane').forEach(p => p.classList.remove('active'));

      tab.classList.add('active');
      const pane = document.getElementById(targetId);
      if (pane) pane.classList.add('active');

      if (targetId === 'tab-history') {
        loadHistory();
      } else if (targetId === 'tab-profile') {
        loadProfile();
      }
    });
  });
}

// Health Check
async function checkHealth() {
  const statusPill = document.getElementById('backend-status');
  try {
    const res = await fetch('/api/health');
    const data = await res.json();
    if (data.status === 'ok') {
      statusPill.innerHTML = `<span class="status-dot"></span> Spring Boot Online (${data.javaVersion || 'Java 21'})`;
      statusPill.style.color = '#4ade80';
    }
  } catch (err) {
    statusPill.innerHTML = `<span class="status-dot" style="background:#ef4444;box-shadow:0 0 8px #ef4444;"></span> Offline`;
    statusPill.style.color = '#f87171';
  }
}

// Setup Event Listeners
function setupEventListeners() {
  // Analyze Form
  const analyzeForm = document.getElementById('analyze-form');
  if (analyzeForm) {
    analyzeForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const url = document.getElementById('input-url').value.trim();
      const title = document.getElementById('input-title').value.trim();
      const text = document.getElementById('input-text').value.trim();

      if (!url && !title && !text) {
        alert('Please provide at least a URL, headline, or article text to analyze.');
        return;
      }

      await executeAnalysis({ url, title, text });
    });
  }

  // Sample Loaders
  document.getElementById('btn-sample-credible')?.addEventListener('click', () => {
    document.getElementById('input-url').value = 'https://bbc.com/news/science-environment-56837908';
    document.getElementById('input-title').value = 'Scientists report climate data consensus in peer-reviewed study';
    document.getElementById('input-text').value = 'By James Gallagher, BBC Health and Science reporter. According to published scientific data and extensive international research, long-term trends have been confirmed by multiple independent academic laboratories.';
  });

  document.getElementById('btn-sample-fake')?.addEventListener('click', () => {
    document.getElementById('input-url').value = 'http://infowars.com/banned-secret-cure';
    document.getElementById('input-title').value = 'SHOCKING BOMBSHELL: YOU WON\'T BELIEVE WHAT DEEP STATE IS HIDING';
    document.getElementById('input-text').value = 'Urgent warning! Hidden truth exposed! They don\'t want you to wake up to this outrageous secret. Doctors are furious because this ancient formula cures everything!';
  });

  // Search Form
  const searchForm = document.getElementById('search-form');
  if (searchForm) {
    searchForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const q = document.getElementById('search-query').value.trim();
      if (!q) return;
      await executeSearch(q);
    });
  }

  // Profile Form
  const profileForm = document.getElementById('profile-form');
  if (profileForm) {
    profileForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      await saveProfile();
    });
  }

  // Clear History
  document.getElementById('btn-clear-history')?.addEventListener('click', async () => {
    if (confirm('Are you sure you want to clear all analysis history?')) {
      await fetch('/api/history', { method: 'DELETE' });
      loadHistory();
      loadProfile();
    }
  });
}

// Execute Analysis
async function executeAnalysis(payload) {
  const resultCard = document.getElementById('analysis-result');
  const analyzeBtn = document.getElementById('btn-analyze');
  analyzeBtn.disabled = true;
  analyzeBtn.innerText = 'Analyzing...';

  try {
    const res = await fetch('/api/analyze', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (!res.ok) {
      throw new Error('Analysis failed');
    }

    const data = await res.json();
    displayAnalysisResult(data);
    loadProfile(); // refresh stats counter
  } catch (err) {
    alert('Error analyzing content: ' + err.message);
  } finally {
    analyzeBtn.disabled = false;
    analyzeBtn.innerText = '🔍 Analyze Credibility';
  }
}

function displayAnalysisResult(data) {
  const resultCard = document.getElementById('analysis-result');
  resultCard.style.display = 'block';

  // Score circle
  const scoreCircle = document.getElementById('result-score-circle');
  const scoreNum = document.getElementById('result-score-number');
  scoreCircle.className = `score-circle ${data.verdictClass}`;
  scoreNum.innerText = data.score;

  // Verdict banner
  const banner = document.getElementById('result-verdict-banner');
  banner.className = `verdict-banner ${data.verdictClass}`;
  banner.innerText = `${data.verdictClass === 'real' ? '✅' : data.verdictClass === 'fake' ? '❌' : '⚠️'} Verdict: ${data.verdict} (${data.score}/100)`;

  // Summary
  document.getElementById('result-summary').innerText = data.summary;

  // Signals
  const signalsList = document.getElementById('result-signals');
  signalsList.innerHTML = '';
  if (data.signals && data.signals.length > 0) {
    data.signals.forEach(s => {
      const item = document.createElement('div');
      item.className = `signal-item ${s.type}`;
      const icon = s.type === 'good' ? '✓' : s.type === 'bad' ? '⚠' : 'ℹ';
      item.innerHTML = `<strong>${icon}</strong> ${escapeHtml(s.label)}`;
      signalsList.appendChild(item);
    });
  }

  // Scroll into view
  resultCard.scrollIntoView({ behavior: 'smooth' });
}

// News Feed
async function loadFeed(topic) {
  // Update topic pill styling
  document.querySelectorAll('.topic-pill').forEach(p => {
    p.classList.toggle('active', p.dataset.topic === topic);
  });

  const container = document.getElementById('feed-container');
  container.innerHTML = '<div style="grid-column: 1/-1; text-align:center; padding: 2rem; color: #94a3b8;">Loading news articles...</div>';

  try {
    const apiKey = currentProfile.apiKey || '';
    const res = await fetch(`/api/news/feed?topics=${encodeURIComponent(topic)}&apikey=${encodeURIComponent(apiKey)}&pageSize=9`);
    const data = await res.json();

    if (data.articles && data.articles.length > 0) {
      renderArticles(data.articles, container);
    } else {
      container.innerHTML = '<div style="grid-column: 1/-1; text-align:center; padding: 2rem;">No articles found for this topic.</div>';
    }
  } catch (err) {
    container.innerHTML = `<div style="grid-column: 1/-1; text-align:center; padding: 2rem; color: #ef4444;">Error loading feed: ${err.message}</div>`;
  }
}

// News Search
async function executeSearch(query) {
  const container = document.getElementById('search-results');
  container.innerHTML = '<div style="grid-column: 1/-1; text-align:center; padding: 2rem; color: #94a3b8;">Searching articles...</div>';

  try {
    const apiKey = currentProfile.apiKey || '';
    const res = await fetch(`/api/news/search?q=${encodeURIComponent(query)}&apikey=${encodeURIComponent(apiKey)}`);
    const data = await res.json();

    if (data.articles && data.articles.length > 0) {
      renderArticles(data.articles, container);
    } else {
      container.innerHTML = `<div style="grid-column: 1/-1; text-align:center; padding: 2rem;">No results found for "${escapeHtml(query)}".</div>`;
    }
  } catch (err) {
    container.innerHTML = `<div style="grid-column: 1/-1; text-align:center; padding: 2rem; color: #ef4444;">Search failed: ${err.message}</div>`;
  }
}

function renderArticles(articles, container) {
  container.innerHTML = '';
  articles.forEach(art => {
    const card = document.createElement('div');
    card.className = 'article-card';

    const defaultImg = 'https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=600';
    const imgSrc = art.image && art.image.trim() !== '' ? art.image : defaultImg;

    card.innerHTML = `
      <img src="${escapeHtml(imgSrc)}" class="article-image" alt="Article Thumbnail" onerror="this.src='${defaultImg}'"/>
      <div class="article-body">
        <div class="article-meta">
          <span>${escapeHtml(art.source || art.domain || 'TruthLens')}</span>
          <span>Score: <strong>${art.score}%</strong></span>
        </div>
        <h3 class="article-title">${escapeHtml(art.title)}</h3>
        <p class="article-desc">${escapeHtml(art.description || 'No description available.')}</p>
        <div class="article-footer">
          <span class="verdict-badge ${art.verdictClass}">${escapeHtml(art.verdict)}</span>
          <a href="${escapeHtml(art.url)}" target="_blank" rel="noopener noreferrer" style="color:#60a5fa; font-size:0.85rem; text-decoration:none;">Read Source ↗</a>
        </div>
      </div>
    `;
    container.appendChild(card);
  });
}

// History
async function loadHistory() {
  const tbody = document.getElementById('history-table-body');
  if (!tbody) return;

  try {
    const res = await fetch('/api/history');
    const data = await res.json();

    tbody.innerHTML = '';
    if (!data || data.length === 0) {
      tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; padding: 2rem;">No past analyses recorded yet. Try analyzing an article!</td></tr>';
      return;
    }

    data.forEach(item => {
      const tr = document.createElement('tr');
      const dateStr = item.timestamp ? new Date(item.timestamp).toLocaleString() : '-';
      tr.innerHTML = `
        <td><strong>#${item.id}</strong></td>
        <td>${escapeHtml(item.domain || 'Text Snippet')}</td>
        <td>
          <span class="verdict-badge ${item.verdictClass}">${escapeHtml(item.verdict)}</span>
        </td>
        <td><strong>${item.score}/100</strong></td>
        <td style="font-size:0.8rem; color:#94a3b8;">${dateStr}</td>
        <td>
          <button class="btn btn-danger btn-sm" onclick="deleteHistory(${item.id})">Delete</button>
        </td>
      `;
      tbody.appendChild(tr);
    });
  } catch (err) {
    console.error('Failed to load history', err);
  }
}

async function deleteHistory(id) {
  try {
    await fetch(`/api/history/${id}`, { method: 'DELETE' });
    loadHistory();
    loadProfile();
  } catch (err) {
    alert('Failed to delete history item');
  }
}

// Profile
async function loadProfile() {
  try {
    const res = await fetch('/api/profile');
    const data = await res.json();
    currentProfile = data;

    // Populate profile inputs
    const nameInput = document.getElementById('profile-name');
    if (nameInput) nameInput.value = data.name || '';

    const apiKeyInput = document.getElementById('profile-apikey');
    if (apiKeyInput) apiKeyInput.value = data.apiKey || '';

    // Checkboxes
    const interests = data.interests || [];
    document.querySelectorAll('.interest-checkbox').forEach(cb => {
      cb.checked = interests.includes(cb.value);
    });

    // Stats
    const totalScanned = data.scanned || 0;
    const fakeCount = data.fakeDetected || 0;
    const credRate = totalScanned > 0 ? Math.round(((totalScanned - fakeCount) / totalScanned) * 100) : 100;

    document.getElementById('stat-scanned').innerText = totalScanned;
    document.getElementById('stat-fake').innerText = fakeCount;
    document.getElementById('stat-rate').innerText = credRate + '%';
  } catch (err) {
    console.error('Failed to load profile', err);
  }
}

async function saveProfile() {
  const name = document.getElementById('profile-name').value.trim();
  const apiKey = document.getElementById('profile-apikey').value.trim();

  const checkedInterests = [];
  document.querySelectorAll('.interest-checkbox:checked').forEach(cb => {
    checkedInterests.push(cb.value);
  });

  const payload = {
    name,
    apiKey,
    interests: checkedInterests
  };

  try {
    const res = await fetch('/api/profile', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    if (res.ok) {
      alert('✓ Profile settings saved successfully!');
      loadProfile();
    }
  } catch (err) {
    alert('Failed to save profile: ' + err.message);
  }
}

function escapeHtml(str) {
  if (!str) return '';
  return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}
