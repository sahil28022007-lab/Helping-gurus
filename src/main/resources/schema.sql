-- Accounts for all three roles. Only a salted PBKDF2 hash of the password is stored, never the password.
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(80) NOT NULL,
  email VARCHAR(120) NOT NULL UNIQUE,
  password_hash VARCHAR(200) NOT NULL,
  role VARCHAR(20) NOT NULL
);
-- Fundraising campaigns. status is REVIEW, LIVE or REJECTED. The four chk_ columns are the admin authenticity checks.
CREATE TABLE IF NOT EXISTS campaigns (
  id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(150) NOT NULL,
  category VARCHAR(50) NOT NULL,
  story TEXT,
  goal DECIMAL(14,2) NOT NULL,
  raised DECIMAL(14,2) NOT NULL DEFAULT 0,
  status VARCHAR(12) NOT NULL DEFAULT 'REVIEW',
  chk_id BOOLEAN NOT NULL DEFAULT FALSE,
  chk_medical BOOLEAN NOT NULL DEFAULT FALSE,
  chk_hospital BOOLEAN NOT NULL DEFAULT FALSE,
  chk_bank BOOLEAN NOT NULL DEFAULT FALSE,
  owner_id INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (owner_id) REFERENCES users(id)
);
-- Many-to-many link between campaigns and the creators who organize them (owner plus invited co-organizers).
CREATE TABLE IF NOT EXISTS campaign_team (
  campaign_id INT NOT NULL,
  user_id INT NOT NULL,
  PRIMARY KEY (campaign_id, user_id),
  FOREIGN KEY (campaign_id) REFERENCES campaigns(id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);
-- Photos uploaded for a campaign. Only APPROVED photos are shown publicly. The image file itself lives in the upload folder.
CREATE TABLE IF NOT EXISTS photos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  campaign_id INT NOT NULL,
  file_name VARCHAR(200) NOT NULL,
  uploaded_by VARCHAR(80) NOT NULL,
  status VARCHAR(10) NOT NULL DEFAULT 'PENDING',
  FOREIGN KEY (campaign_id) REFERENCES campaigns(id)
);
-- Every contribution. donor_name holds "Anonymous Guru" for anonymous gifts. Written together with campaigns.raised in one transaction.
CREATE TABLE IF NOT EXISTS donations (
  id INT AUTO_INCREMENT PRIMARY KEY,
  campaign_id INT NOT NULL,
  donor_id INT NOT NULL,
  donor_name VARCHAR(80) NOT NULL,
  amount DECIMAL(14,2) NOT NULL,
  message VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (campaign_id) REFERENCES campaigns(id),
  FOREIGN KEY (donor_id) REFERENCES users(id)
);
-- Organizer updates (kind UPDATE) and supporter comments (kind COMMENT) shown on the campaign page.
CREATE TABLE IF NOT EXISTS posts (
  id INT AUTO_INCREMENT PRIMARY KEY,
  campaign_id INT NOT NULL,
  author VARCHAR(80) NOT NULL,
  kind VARCHAR(10) NOT NULL,
  body VARCHAR(500) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (campaign_id) REFERENCES campaigns(id)
);
