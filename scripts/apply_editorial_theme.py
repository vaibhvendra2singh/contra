import os

css_content = '''@import url('https://fonts.googleapis.com/css2?family=Bebas+Neue&family=Space+Grotesk:wght@400;500;600;700&family=Space+Mono:wght@400;500;700&display=swap');

:root {
  --canvas: #dfa7de;
  --canvas-light: #edd3ed;
  --canvas-card: #f8eff8;
  --white: #ffffff;
  --plum: #2b083b;
  --plum-dark: #1b0326;
  --orange: #ff5500;
  --orange-hover: #e64d00;
  --muted: #6e4977;
  --line: rgba(43, 8, 59, 0.14);
  --shadow: 0 16px 40px rgba(43, 8, 59, 0.08);
  --green: #2b083b;
  --lime: #ff5500;
}

* { box-sizing: border-box; }

body {
  margin: 0;
  background: var(--canvas);
  color: var(--plum);
  font-family: 'Space Grotesk', -apple-system, BlinkMacSystemFont, sans-serif;
  -webkit-font-smoothing: antialiased;
}

button, input, select {
  font: inherit;
}

input, select {
  border: 1px solid var(--line);
  border-radius: 12px;
  padding: 10px 14px;
  outline: none;
  background: white;
  transition: border-color .2s;
}

input:focus, select:focus {
  border-color: var(--plum);
}

button {
  cursor: pointer;
}

.topbar-wrapper {
  position: sticky;
  top: 14px;
  z-index: 100;
  display: flex;
  justify-content: center;
  padding: 0 clamp(16px, 3vw, 48px);
  pointer-events: none;
}

.topbar {
  pointer-events: auto;
  width: 100%;
  max-width: 1340px;
  height: 64px;
  background: rgba(250, 240, 250, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(43, 8, 59, 0.16);
  border-radius: 9999px;
  box-shadow: 0 16px 36px -4px rgba(43, 8, 59, 0.1), 0 4px 16px -2px rgba(43, 8, 59, 0.04);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 10px 0 22px;
  transition: all .25s ease;
}

.brand {
  color: var(--plum);
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 700;
  letter-spacing: .08em;
  font-size: 14px;
}

.brand-logo {
  display: grid;
  place-items: center;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: var(--canvas-card);
  border: 1px solid var(--line);
  transition: transform .35s cubic-bezier(.4, 0, .2, 1);
  box-shadow: 0 2px 6px rgba(43, 8, 59, 0.04);
}

.brand:hover .brand-logo {
  transform: rotate(45deg);
  border-color: var(--orange);
}

.brand-text {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 700;
  letter-spacing: .08em;
}

.brand-badge {
  background: rgba(43, 8, 59, 0.08);
  color: var(--plum);
  font: 9px 'Space Mono', monospace;
  letter-spacing: .08em;
  padding: 3px 7px;
  border-radius: 9999px;
  font-weight: 700;
}

nav {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(43, 8, 59, 0.06);
  padding: 4px;
  border-radius: 9999px;
}

.nav-link {
  border: 0;
  background: transparent;
  color: var(--plum);
  padding: 7px 16px;
  border-radius: 9999px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: .05em;
  text-transform: uppercase;
  display: inline-flex;
  align-items: center;
  gap: 7px;
  transition: all .2s cubic-bezier(.4, 0, .2, 1);
}

.nav-link .nav-icon {
  transition: transform .2s ease;
  opacity: .8;
}

.nav-link:hover {
  color: var(--plum-dark);
  background: rgba(255, 255, 255, 0.65);
}

.nav-link:hover .nav-icon {
  opacity: 1;
  transform: scale(1.08);
}

.nav-link.active {
  background: var(--plum);
  color: white;
  box-shadow: 0 4px 12px rgba(43, 8, 59, 0.3);
}

.nav-link.active .nav-icon {
  opacity: 1;
  stroke: white;
}

.topbar-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.cart-pill {
  border: 0;
  border-radius: 9999px;
  background: var(--orange);
  color: white;
  padding: 8px 16px 8px 14px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: .04em;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 6px 18px rgba(255, 85, 0, 0.35);
  transition: all .2s ease;
}

.cart-pill:hover {
  background: var(--orange-hover);
  transform: translateY(-1px) scale(1.02);
  box-shadow: 0 8px 22px rgba(255, 85, 0, 0.45);
}

.cart-icon { stroke: white; }

.cart-badge {
  background: white;
  color: var(--orange);
  font-weight: 800;
  font-size: 11px;
  padding: 2px 7px;
  border-radius: 9999px;
  min-width: 18px;
  text-align: center;
}

.cart-arrow {
  font-size: 14px;
  line-height: 1;
  opacity: .85;
}

.page { display: none; }
.page.active { display: block; }

/* HERO - Retro-futuristic Editorial Style */
.hero {
  margin: 16px clamp(14px, 4vw, 56px) 0;
  border-radius: 28px;
  background: var(--canvas);
  color: var(--plum);
  display: grid;
  grid-template-columns: 1.15fr 0.85fr;
  align-items: center;
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(43, 8, 59, 0.16);
  box-shadow: 0 20px 50px rgba(43, 8, 59, 0.08);
  min-height: 580px;
}

.hero-copy {
  padding: clamp(36px, 6vw, 75px);
  z-index: 2;
}

.hero-eyebrow-line {
  margin-bottom: 20px;
}

.hero .eyebrow {
  font: 11px/1 'Space Mono', monospace;
  letter-spacing: .2em;
  color: var(--plum);
  text-transform: uppercase;
  font-weight: 700;
  margin: 0;
  display: inline-block;
  border-bottom: 2px solid var(--plum);
  padding-bottom: 6px;
}

.hero-display-title {
  font-family: 'Bebas Neue', sans-serif;
  font-size: clamp(84px, 11vw, 150px);
  line-height: 0.88;
  letter-spacing: .03em;
  margin: 0 0 22px;
  color: var(--plum);
  text-transform: uppercase;
}

.hero-tagline {
  font: 12.5px/1.8 'Space Mono', monospace;
  letter-spacing: .12em;
  text-transform: uppercase;
  color: var(--plum);
  max-width: 440px;
  margin: 0 0 32px;
  font-weight: 600;
  opacity: .9;
}

.button-hero-outline {
  border: 1.5px solid var(--plum);
  background: transparent;
  color: var(--plum);
  padding: 13px 26px;
  border-radius: 9999px;
  font: 11px 'Space Mono', monospace;
  letter-spacing: .15em;
  font-weight: 700;
  text-transform: uppercase;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 14px;
  transition: all .25s ease;
}

.button-hero-outline:hover {
  background: var(--plum);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(43, 8, 59, 0.25);
}

.hero-art-photo {
  height: 100%;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  overflow: hidden;
  position: relative;
}

.hero-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center top;
}

.button {
  border: 0;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 12px 22px;
  font-size: 12px;
  font-weight: 600;
  border-radius: 9999px;
  transition: .2s cubic-bezier(.4, 0, .2, 1);
}

.button:hover { transform: translateY(-2px); }
.button-light { background: var(--orange); color: white; box-shadow: 0 4px 14px rgba(255, 85, 0, 0.3); }
.button-light:hover { background: var(--orange-hover); box-shadow: 0 6px 18px rgba(255, 85, 0, 0.4); }
.button-dark { background: var(--plum); color: white; box-shadow: 0 4px 14px rgba(43, 8, 59, 0.25); }
.button-dark:hover { background: var(--plum-dark); }
.button-wide { width: 100%; }

.section {
  padding: 80px clamp(24px, 8vw, 120px);
}

.section-heading {
  display: flex;
  justify-content: space-between;
  align-items: end;
  margin-bottom: 32px;
}

.section-heading h2 {
  font-family: 'Bebas Neue', sans-serif;
  font-size: 46px;
  letter-spacing: .05em;
  color: var(--plum);
  margin: 0;
  text-transform: uppercase;
}

.eyebrow {
  font: 10px 'Space Mono', monospace;
  letter-spacing: .16em;
  color: var(--plum);
  text-transform: uppercase;
  margin: 0 0 10px;
  font-weight: 700;
}

.muted {
  font-size: 12px;
  color: var(--muted);
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.product-card {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  border: 1px solid var(--line);
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(43, 8, 59, 0.06);
  transition: all .25s ease;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 40px rgba(43, 8, 59, 0.12);
  border-color: rgba(43, 8, 59, 0.25);
}

.product-image {
  height: 240px;
  display: grid;
  place-items: center;
  position: relative;
  overflow: hidden;
}

.product-image span {
  position: relative;
  z-index: 1;
  font-family: 'Bebas Neue', sans-serif;
  font-size: 72px;
  color: rgba(255, 255, 255, 0.85);
  letter-spacing: .05em;
}

.product-image.mug { background: linear-gradient(135deg, #ff5500 0%, #ff7733 100%); }
.product-image.tote { background: linear-gradient(135deg, #2b083b 0%, #52156d 100%); }
.product-image.tee { background: linear-gradient(135deg, #b046a6 0%, #d870cf 100%); }

.product-info {
  padding: 22px 24px;
}

.product-info h3 {
  font-family: 'Space Grotesk', sans-serif;
  font-weight: 700;
  font-size: 20px;
  color: var(--plum);
  margin: 0 0 6px;
}

.product-info p {
  font-size: 12.5px;
  color: var(--muted);
  line-height: 1.6;
  min-height: 38px;
  margin: 0 0 14px;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 14px;
}

.product-meta strong {
  font: 18px 'Space Mono', monospace;
  font-weight: 700;
  color: var(--plum);
}

.product-meta button {
  background: var(--plum);
  color: white;
  border: 0;
  padding: 10px 20px;
  font-size: 11.5px;
  font-weight: 700;
  letter-spacing: .06em;
  text-transform: uppercase;
  border-radius: 9999px;
  transition: all .2s ease;
}

.product-meta button:hover {
  background: var(--orange);
  transform: translateY(-1px);
  box-shadow: 0 4px 14px rgba(255, 85, 0, 0.35);
}

.stock-label {
  font: 11px 'Space Mono', monospace;
  color: var(--muted);
  margin-top: 10px;
}

.process-strip {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border: 1px solid var(--line);
  border-radius: 24px;
  margin: 0 clamp(24px, 8vw, 120px) 70px;
  padding: 34px 40px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 28px;
}

.process-strip div span {
  font: 11px 'Space Mono', monospace;
  color: var(--orange);
  font-weight: 700;
  display: block;
  margin-bottom: 6px;
}

.process-strip div b {
  font-size: 15px;
  color: var(--plum);
  display: block;
  margin-bottom: 4px;
}

.process-strip div small {
  color: var(--muted);
  font-size: 12px;
}

/* SUBHERO & OPERATIONS */
.subhero {
  background: rgba(255, 255, 255, 0.55);
  backdrop-filter: blur(10px);
  border: 1px solid var(--line);
  border-radius: 24px;
  padding: 45px clamp(24px, 7vw, 100px);
  margin: 16px clamp(14px, 4vw, 56px) 28px;
}

.subhero h1 {
  font-family: 'Bebas Neue', sans-serif;
  font-size: clamp(48px, 6vw, 68px);
  letter-spacing: .04em;
  color: var(--plum);
  margin: 0 0 12px;
  line-height: 1;
}

.subhero p:not(.eyebrow) {
  color: var(--muted);
  max-width: 540px;
  font-size: 14px;
  line-height: 1.7;
  margin: 0;
}

.admin-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin: 0 clamp(24px, 7vw, 100px) 32px;
}

.metric-card {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  border: 1px solid var(--line);
  border-radius: 20px;
  padding: 22px;
  box-shadow: 0 4px 18px rgba(43, 8, 59, 0.04);
}

.metric-card span {
  font: 10px 'Space Mono', monospace;
  color: var(--muted);
  text-transform: uppercase;
  letter-spacing: .1em;
  font-weight: 700;
}

.metric-card strong {
  display: block;
  font: 34px 'Space Mono', monospace;
  color: var(--plum);
  margin-top: 10px;
}

.admin-section {
  margin: 0 clamp(24px, 7vw, 100px) 40px;
}

.admin-section-heading {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 0 0 16px;
}

.admin-section-heading h2 {
  font-family: 'Bebas Neue', sans-serif;
  font-size: 28px;
  letter-spacing: .04em;
  color: var(--plum);
  margin: 0;
}

.admin-section-heading span {
  font: 10px 'Space Mono', monospace;
  color: var(--muted);
  font-weight: 700;
}

.admin-card {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  border: 1px solid var(--line);
  border-radius: 22px;
  padding: 24px;
  box-shadow: 0 6px 20px rgba(43, 8, 59, 0.04);
}

.admin-two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin: 0 clamp(24px, 7vw, 100px) 36px;
}

.admin-two-col .admin-card { margin-bottom: 0; }

.table-wrap {
  overflow: auto;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid var(--line);
  border-radius: 18px;
  box-shadow: 0 4px 16px rgba(43, 8, 59, 0.03);
}

table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

th, td {
  padding: 14px 18px;
  border-bottom: 1px solid rgba(43, 8, 59, 0.08);
  font-size: 12px;
}

th {
  font: 10px 'Space Mono', monospace;
  color: var(--muted);
  text-transform: uppercase;
  letter-spacing: .1em;
}

.order-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border: 1px solid var(--line);
  border-radius: 22px;
  padding: 22px 26px;
  margin-bottom: 18px;
  box-shadow: 0 6px 20px rgba(43, 8, 59, 0.04);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.order-header h2 {
  font-family: 'Space Grotesk', sans-serif;
  font-size: 20px;
  font-weight: 700;
  color: var(--plum);
  margin: 0 0 6px;
}

.status-pill {
  font: 10px 'Space Mono', monospace;
  font-weight: 700;
  background: rgba(43, 8, 59, 0.08);
  color: var(--plum);
  padding: 5px 12px;
  border-radius: 9999px;
  text-transform: uppercase;
  letter-spacing: .06em;
}

.status-pill.status-done, .status-pill.status-delivered { background: #e3fcef; color: #006644; }
.status-pill.status-in-progress, .status-pill.status-shipped { background: #deebff; color: #0747a6; }
.status-pill.status-to-do { background: #f4f5f7; color: #42526e; }
.status-pill.status-cancelled, .status-pill.status-payment-failed { background: #ffebe6; color: #de350b; }
.status-pill.status-refunded { background: #eae6ff; color: #403294; }
.status-pill.status-inventory-allocated, .status-pill.status-inventory_allocated { background: #fff0b3; color: #172b4d; }

.order-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 18px;
}

.order-actions button {
  border: 1px solid var(--plum);
  background: white;
  padding: 9px 18px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: .05em;
  border-radius: 9999px;
  color: var(--plum);
  transition: all .2s ease;
}

.order-actions button:hover {
  background: var(--plum);
  color: white;
  transform: translateY(-1px);
}

.feature-flags {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}

.flag-row {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid var(--line);
  border-radius: 18px;
  padding: 18px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 14px rgba(43, 8, 59, 0.03);
}

.flag-row b {
  font-size: 13px;
  color: var(--plum);
}

.flag-row small {
  display: block;
  color: var(--muted);
  font-size: 10.5px;
  margin-top: 4px;
  max-width: 220px;
}

.switch {
  width: 42px;
  height: 24px;
  border-radius: 20px;
  background: #c4ccc3;
  border: 0;
  padding: 3px;
  transition: .2s;
}

.switch:after {
  content: '';
  display: block;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: white;
  transition: .2s;
}

.switch.on { background: var(--orange); }
.switch.on:after { transform: translateX(18px); }

/* JIRA PAGE */
.jira-hero-banner {
  border-radius: 26px;
  background: linear-gradient(135deg, #1f052b 0%, #2d0a3d 50%, #46145a 100%);
  color: white;
  box-shadow: 0 20px 50px rgba(31, 5, 43, 0.25);
  margin: 0 clamp(24px, 7vw, 100px) 32px;
  padding: 34px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
}

.jira-hero-banner.offline { background: linear-gradient(135deg, #253858 0%, #42526e 100%); }

.jira-hero-left h2 {
  font-family: 'Bebas Neue', sans-serif;
  font-size: 44px;
  letter-spacing: .04em;
  margin: 0 0 8px;
  color: #fff;
}

.jira-goal {
  margin: 0;
  color: #f0d4f5;
  font-size: 13px;
  max-width: 520px;
  line-height: 1.6;
}

.jira-status-pill {
  background: rgba(255, 255, 255, 0.15);
  color: #79f2c0;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 9999px;
  font: 10px 'Space Mono', monospace;
  padding: 4px 12px;
  letter-spacing: .08em;
  display: inline-block;
  margin-bottom: 12px;
}

.jira-hero-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.jira-stat-box {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 18px;
  padding: 16px 24px;
  text-align: center;
}

.jira-stat-box span {
  display: block;
  font: 9px 'Space Mono', monospace;
  letter-spacing: .1em;
  color: #f0d4f5;
  margin-bottom: 4px;
}

.jira-stat-box strong {
  font: 24px 'Space Mono', monospace;
  color: #fff;
}

.jira-sections-grid {
  display: grid;
  grid-template-columns: 1.05fr .95fr;
  gap: 24px;
  margin: 0 clamp(24px, 7vw, 100px) 40px;
}

.jira-sections-grid .admin-card { margin-bottom: 0; }

.readiness-banner {
  border-radius: 16px;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.readiness-banner.ready { background: #e3fcef; border: 1px solid #abf5d1; color: #006644; }
.readiness-banner.unready { background: #fff0b3; border: 1px solid #ffe380; color: #172b4d; }

.pill-select {
  border-radius: 9999px;
  padding: 5px 14px;
  font-size: 11px;
  font-weight: 700;
  border: 1px solid var(--line);
  background: white;
  cursor: pointer;
}

.jira-issue-link {
  color: var(--orange);
  text-decoration: none;
  font-weight: 700;
}

.jira-issue-link:hover { text-decoration: underline; }

.notice-list { display: grid; gap: 8px; }

.notice-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  background: white;
  border: 1px solid var(--line);
  border-radius: 14px;
  padding: 14px 18px;
  font-size: 11.5px;
  box-shadow: 0 2px 8px rgba(43, 8, 59, 0.02);
}

.timeline {
  border-left: 2px solid var(--plum);
  margin: 25px 0 0 7px;
  padding-left: 24px;
  display: grid;
  gap: 20px;
}

.timeline-item { position: relative; }
.timeline-item:before {
  content: '';
  position: absolute;
  width: 10px;
  height: 10px;
  background: var(--orange);
  border: 3px solid white;
  outline: 1px solid var(--orange);
  border-radius: 50%;
  left: -31px;
  top: 3px;
}

.timeline-item b { font-size: 13px; color: var(--plum); }
.timeline-item small { display: block; color: var(--muted); font: 10px 'Space Mono', monospace; margin-top: 5px; }

/* LOOKUP & FORM */
.lookup-form {
  margin: 0 clamp(24px, 7vw, 100px) 40px;
}

.input-row {
  display: flex;
  gap: 10px;
  margin-top: 8px;
}

.input-row input {
  flex: 1;
  border-radius: 9999px;
  padding: 14px 22px;
  font-size: 14px;
}

.result-area {
  margin: 0 clamp(24px, 7vw, 100px) 50px;
}

/* DIALOG / MODAL */
dialog {
  border: 0;
  border-radius: 28px;
  width: min(580px, calc(100vw - 28px));
  padding: 34px 38px;
  box-shadow: 0 25px 80px rgba(31, 5, 43, 0.35);
  background: var(--white);
}

dialog::backdrop {
  background: rgba(31, 5, 43, 0.65);
  backdrop-filter: blur(6px);
}

dialog h2 {
  font-family: 'Bebas Neue', sans-serif;
  font-size: 38px;
  letter-spacing: .04em;
  color: var(--plum);
  margin: 0 0 20px;
}

.dialog-close { display: flex; justify-content: flex-end; }
.dialog-close button { background: none; border: 0; font-size: 24px; color: var(--muted); }

.checkout-form { display: grid; gap: 16px; }
.form-split { display: grid; grid-template-columns: .7fr 1.3fr; gap: 12px; }
.form-hint { font-size: 10px; color: var(--muted); margin: 0; }

.checkout-total {
  border-top: 1px solid var(--line);
  padding: 16px 0;
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: var(--plum);
}

.checkout-total strong { font: 18px 'Space Mono', monospace; color: var(--plum); }

.toast {
  position: fixed;
  bottom: 24px;
  left: 50%;
  transform: translate(-50%, 18px);
  background: var(--plum);
  color: white;
  padding: 14px 22px;
  font-size: 12px;
  font-weight: 600;
  border-radius: 9999px;
  box-shadow: 0 10px 30px rgba(43, 8, 59, 0.3);
  opacity: 0;
  pointer-events: none;
  transition: .2s;
  z-index: 1000;
  max-width: 90vw;
}

.toast.show { opacity: 1; transform: translate(-50%, 0); }

.empty {
  padding: 28px;
  background: white;
  color: var(--muted);
  font-size: 12px;
  border: 1px solid var(--line);
  border-radius: 18px;
  text-align: center;
}

.loading {
  padding: 50px;
  color: var(--muted);
  text-align: center;
  font-size: 13px;
}

footer {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  align-items: center;
  border-top: 1px solid var(--line);
  padding: 28px clamp(24px, 6vw, 90px);
  font-size: 11px;
  color: var(--muted);
  background: rgba(255, 255, 255, 0.4);
}

footer a { color: var(--plum); text-decoration: none; font-weight: 700; }

@media(max-width: 760px) {
  .topbar-wrapper { top: 8px; padding: 0 8px; }
  .topbar { height: 52px; padding: 0 6px 0 10px; border-radius: 30px; }
  .brand { font-size: 11px; gap: 6px; }
  .brand-logo { width: 28px; height: 28px; }
  .brand-logo svg { width: 18px; height: 18px; }
  .brand-badge { display: none; }
  nav { gap: 2px; padding: 2px; }
  .nav-link { padding: 6px 9px; font-size: 10.5px; }
  .nav-link .nav-icon { width: 13px; height: 13px; }
  .cart-pill { padding: 6px 10px 6px 8px; font-size: 11px; }
  .cart-arrow { display: none; }
  .hero { grid-template-columns: 1fr; min-height: 520px; margin: 0 10px; }
  .hero-copy { padding: 40px 24px 30px; }
  .hero-art-photo { max-height: 320px; }
  .product-grid { grid-template-columns: 1fr; }
  .process-strip { grid-template-columns: 1fr; }
  .dashboard-grid { grid-template-columns: repeat(2, 1fr); margin: 0 16px 24px; }
  .admin-section { margin: 0 16px 28px; }
  .admin-two-col, .jira-sections-grid { grid-template-columns: 1fr; margin: 0 16px 24px; }
  .jira-hero-banner { flex-direction: column; align-items: flex-start; padding: 24px; margin: 0 16px 24px; }
  .jira-hero-right { width: 100%; justify-content: space-between; margin-top: 16px; }
  .feature-flags { grid-template-columns: 1fr; }
}
'''

with open('src/main/resources/static/styles.css', 'w') as f:
    f.write(css_content)

print("Generated complete editorial retro-futuristic styles.css successfully!")

