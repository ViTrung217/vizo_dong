(function () {
  const STORAGE_KEY = 'vz-theme';

  function applyTheme(theme) {
    const html = document.documentElement;
    if (theme === 'dark') {
      html.setAttribute('data-theme', 'dark');
    } else {
      html.removeAttribute('data-theme');
    }
  }

  function getInitialTheme() {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved === 'dark' || saved === 'light') {
      return saved;
    }
    const prefersDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
    return prefersDark ? 'dark' : 'light';
  }

  function currentTheme() {
    return document.documentElement.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
  }

  function buildToggleButton() {
    const btn = document.createElement('button');
    btn.type = 'button';
    btn.className = 'theme-toggle-btn';

    const updateLabel = () => {
      btn.textContent = currentTheme() === 'dark' ? '☀️ Sáng' : '🌙 Tối';
    };

    btn.addEventListener('click', function () {
      const next = currentTheme() === 'dark' ? 'light' : 'dark';
      applyTheme(next);
      localStorage.setItem(STORAGE_KEY, next);
      updateLabel();
    });

    updateLabel();
    document.body.appendChild(btn);
  }

  applyTheme(getInitialTheme());

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', buildToggleButton);
  } else {
    buildToggleButton();
  }
})();
