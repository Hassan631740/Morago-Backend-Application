# 🚂 Deploy Morago Backend to Railway - Complete Guide

**⏱️ Estimated time: 10 minutes**

---

## ✅ Pre-Deployment Checklist

Your application is **ready to deploy**! These files are configured:
- ✅ `nixpacks.toml` - Railway build configuration
- ✅ `railway.json` - Deployment settings
- ✅ `.railwayignore` - Files to exclude
- ✅ `pom.xml` - Added Spring Boot Actuator
- ✅ `application-prod.properties` - Production configuration
- ✅ `.gitignore` - Updated to protect secrets

---

## 🚀 Step-by-Step Deployment

### Step 1: Generate Secure Secrets (2 minutes)

Run the secrets generator script:
```bash
./generate-railway-secrets.sh
```

This creates a file `railway-secrets-YYYYMMDD-HHMMSS.txt` with:
- Strong JWT secret (64+ characters)
- Backup JWT secret
- Random admin password
- Complete environment variables ready to copy

**Important:** Keep this file safe and don't commit it to Git!

---

### Step 2: Push Your Code to GitHub (1 minute)

```bash
git add .
git commit -m "Configure Railway deployment"
git push origin main
```

---

### Step 3: Create Railway Project (2 minutes)

1. Go to **[railway.app](https://railway.app)**
2. Sign up or log in (can use GitHub account)
3. Click **"New Project"**
4. Select **"Deploy from GitHub repo"**
5. Choose your repository: `morago-backend-sep15`
6. Railway detects it's a Java/Maven project ✅

---

### Step 4: Add MySQL Database (1 minute)

In your Railway project:
1. Click **"New"** button
2. Select **"Database"** → **"Add MySQL"**
3. Wait ~30 seconds for provisioning
4. MySQL is now available! Railway auto-generates these variables:
   - `MYSQLHOST`
   - `MYSQLPORT`
   - `MYSQLDATABASE`
   - `MYSQLUSER`
   - `MYSQLPASSWORD`

---

### Step 5: Configure Environment Variables (3 minutes)

1. Click on your **morago-backend service** (not the database)
2. Go to **"Variables"** tab
3. Click **"New Variable"** for each:

#### Required Variables (copy from your generated secrets file):

```bash
SPRING_PROFILES_ACTIVE=prod
```

```bash
DB_URL=jdbc:mysql://${{MYSQLHOST}}:${{MYSQLPORT}}/${{MYSQLDATABASE}}?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

```bash
DB_USERNAME=${{MYSQLUSER}}
```

```bash
DB_PASSWORD=${{MYSQLPASSWORD}}
```

```bash
JWT_EXPIRATION=86400000
```

```bash
JWT_EXPIRATION_MS=3600000
```

```bash
SECURITY_JWT_SECRET=<paste-from-generated-file>
```

```bash
SOCKETIO_HOST=0.0.0.0
```

```bash
SOCKETIO_PORT=9092
```

```bash
SOCKETIO_ALLOWED_ORIGINS=https://your-frontend-domain.com,http://localhost:5173
```

**Important:** Update `SOCKETIO_ALLOWED_ORIGINS` with your actual frontend URL!

```bash
MAX_FILE_SIZE=10MB
```

```bash
MAX_REQUEST_SIZE=10MB
```

#### Optional Variables (for AWS S3 file storage):

```bash
AWS_S3_BUCKET=your-bucket-name
AWS_S3_REGION=us-east-1
AWS_S3_ACCESS_KEY=your-access-key
AWS_S3_SECRET_KEY=your-secret-key
AWS_S3_BASE_URL=https://your-bucket.s3.amazonaws.com
```

---

### Step 6: Deploy! (1 minute)

Railway automatically starts building and deploying your application.

**Monitor progress:**
1. Go to **"Deployments"** tab
2. Click on the latest deployment
3. View logs in real-time

**What Railway does:**
1. ✅ Pulls your code from GitHub
2. ✅ Runs: `mvn clean package -DskipTests`
3. ✅ Creates executable JAR
4. ✅ Runs Flyway database migrations
5. ✅ Starts your application
6. ✅ Exposes on HTTPS with SSL

**Wait for:** `Deployment successful!` message (usually 2-5 minutes)

---

### Step 7: Verify Deployment (2 minutes)

#### Get Your Railway URL:
- Found in Railway dashboard under **"Settings"** → **"Domains"**
- Usually looks like: `https://morago-backend-production-XXXX.up.railway.app`

#### Test Health Check:
```bash
curl https://your-app.up.railway.app/actuator/health
```

**Expected response:**
```json
{"status":"UP"}
```

#### Test Swagger UI:
Open in browser:
```
https://your-app.up.railway.app/swagger-ui.html
```

You should see the API documentation! 🎉

#### Test Admin Endpoints:
```bash
# Get all blocked users (requires auth)
curl https://your-app.up.railway.app/api/admin/users/blocked \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 🎯 Post-Deployment Configuration

### 1. Update Frontend Configuration

Update your frontend to use the Railway backend URL:

```javascript
// Before (local)
const API_URL = 'http://localhost:8080'
const SOCKET_URL = 'http://localhost:9092'

// After (production)
const API_URL = 'https://your-app.up.railway.app'
const SOCKET_URL = 'https://your-app.up.railway.app:9092'
```

### 2. Update CORS Origins

Go back to Railway Variables and update:
```bash
SOCKETIO_ALLOWED_ORIGINS=https://your-actual-frontend.com
```

Remove `http://localhost:5173` from production.

### 3. Set Up Custom Domain (Optional)

In Railway:
1. Go to **"Settings"** → **"Domains"**
2. Click **"Custom Domain"**
3. Add your domain (e.g., `api.morago.com`)
4. Update DNS records as shown:
   - Type: `CNAME`
   - Name: `api` (or `@` for root)
   - Value: `<your-railway-url>.railway.app`
5. Wait for SSL certificate (automatic, ~5 minutes)

---

## 📊 Available Endpoints

After deployment, these endpoints are available:

### Health & Monitoring:
- `GET /actuator/health` - Health check
- `GET /actuator/info` - Application info
- `GET /actuator/metrics` - Metrics (admin only)

### API Documentation:
- `GET /swagger-ui.html` - Interactive API docs
- `GET /v3/api-docs` - OpenAPI JSON

### Authentication:
- `POST /api/auth/login` - User login
- `POST /api/auth/signup` - User registration

### Admin Features:
- `POST /api/admin/users/{id}/block` - Block user account
- `POST /api/admin/users/{id}/activate` - Activate user account
- `GET /api/admin/users/blocked` - Get blocked users
- `POST /api/admin/translator-profiles/{id}/verify` - Verify translator
- `GET /api/admin/translator-profiles/unverified` - Get unverified translators

---

## 🔍 Monitoring & Maintenance

### View Logs:
1. Railway Dashboard → **"Logs"** tab
2. Real-time application logs
3. Filter by level (info, warn, error)

### Monitor Metrics:
1. Railway Dashboard → **"Metrics"** tab
2. CPU usage, memory usage, network traffic
3. Set up alerts for high usage

### Database Management:
1. Click on **MySQL service**
2. Connect using Railway CLI: `railway connect`
3. Or use external tools (MySQL Workbench, DBeaver)

### Application Metrics:
```bash
# Get detailed metrics (requires admin auth)
curl https://your-app.up.railway.app/actuator/metrics \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

---

## 🐛 Troubleshooting

### Build Fails ❌

**Check logs:** Railway → Deployments → Latest → View Logs

**Common issues:**
- Missing dependencies → Check `pom.xml`
- Java version mismatch → Verify `nixpacks.toml` specifies Java 21
- Out of memory → Increase Railway plan

**Fix:**
```bash
# Test build locally first
mvn clean package -DskipTests

# If successful, push to GitHub
git push origin main
```

### Deployment Crashes ❌

**Check environment variables:**
- Verify all required variables are set
- Check for typos in variable names
- Ensure `DB_URL` uses Railway's MySQL variables

**Check database connection:**
```bash
# View Railway MySQL variables
Railway → MySQL Service → Variables

# Ensure they're correctly referenced:
DB_URL=jdbc:mysql://${{MYSQLHOST}}:${{MYSQLPORT}}/${{MYSQLDATABASE}}?useSSL=true...
```

### Health Check Fails ❌

**Verify Actuator:**
```bash
# Check if endpoint is accessible
curl https://your-app.up.railway.app/actuator/health -v

# Should return 200 OK with {"status":"UP"}
```

**Check logs:**
- Look for startup errors
- Verify Flyway migrations completed
- Check database connection

### Database Connection Issues ❌

**Verify MySQL is running:**
- Railway → MySQL Service → Status should be "Active"

**Check connection string:**
```bash
# Should use Railway variables, not hardcoded values
DB_URL=jdbc:mysql://${{MYSQLHOST}}:${{MYSQLPORT}}/${{MYSQLDATABASE}}?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

**Test connection:**
- Railway → MySQL Service → "Connect"
- Use Railway CLI to test database access

### 502 Bad Gateway ❌

**Application crashed or not responding:**
1. Check logs for errors
2. Verify port binding: app should use `$PORT` variable
3. Check if application started successfully
4. Look for OutOfMemoryError or similar

**Fix:**
- Railway auto-restarts on failure (max 3 retries)
- Check if resources are sufficient
- Review application logs for stack traces

---

## 💰 Cost Estimation

### Railway Free Tier:
- **$5/month usage credit** ✅
- MySQL database included
- SSL certificates included
- Custom domains supported

### Expected Costs:
- **Hobby/Test**: $0-5/month (within free tier)
- **Small Production**: $5-15/month
- **Medium Production**: $15-30/month

### Monitor Usage:
- Railway Dashboard → **"Usage"** tab
- View current month's usage
- Set up billing alerts

---

## 🔐 Security Best Practices

### Before Going Live:

1. ✅ **Strong JWT Secret**
   - At least 64 characters
   - Generated using `openssl rand -base64 64`
   - Never reuse secrets across environments

2. ✅ **Environment Variables**
   - All secrets in Railway Variables (not in code)
   - No hardcoded passwords or API keys
   - Verify `.gitignore` excludes secrets files

3. ✅ **CORS Configuration**
   - Only allow your actual frontend domains
   - Remove `localhost` from production
   - Update `SOCKETIO_ALLOWED_ORIGINS`

4. ✅ **Database Security**
   - Use Railway's managed MySQL (secure by default)
   - SSL enabled in connection string
   - Regular backups (Railway provides automatic backups)

5. ✅ **HTTPS Only**
   - Railway provides SSL automatically
   - Enforce secure cookies: `server.servlet.session.cookie.secure=true`

### Regular Maintenance:

- 🔄 **Rotate JWT secrets** quarterly
- 💾 **Backup database** weekly
- 📊 **Monitor logs** daily
- 🔒 **Update dependencies** monthly
- 🔍 **Review access logs** for suspicious activity

---

## 🔄 Continuous Deployment

Railway automatically redeploys on every Git push:

```bash
# Make changes
git add .
git commit -m "Update feature"
git push origin main

# Railway automatically:
# 1. Detects the push
# 2. Starts new deployment
# 3. Builds application
# 4. Runs tests (if configured)
# 5. Deploys if successful
# 6. Switches traffic to new version
```

### Deployment Branches:
- `main` branch → Production
- `dev` branch → Staging (optional, create separate Railway project)

---

## 📚 Additional Resources

### Your Documentation:
- 📖 `RAILWAY_ENV_VARIABLES.txt` - Environment variable reference
- 📖 `RAILWAY_SETUP_COMPLETE.md` - Setup completion summary
- 📖 `README.md` - Project overview
- 📖 `API_DOCUMENTATION.md` - API reference

### Railway Resources:
- 📖 [Railway Documentation](https://docs.railway.app)
- 🎓 [Railway Guides](https://docs.railway.app/guides)
- 💬 [Railway Discord Community](https://discord.gg/railway)
- 🐦 [Railway Twitter](https://twitter.com/Railway)

### Spring Boot Resources:
- 📖 [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- 📖 [Spring Boot on Railway](https://docs.railway.app/guides/spring-boot)

---

## ✅ Deployment Checklist

Use this checklist to ensure successful deployment:

- [ ] Code is pushed to GitHub
- [ ] Railway project created
- [ ] MySQL database added to Railway
- [ ] All environment variables set correctly
- [ ] `SECURITY_JWT_SECRET` generated with openssl
- [ ] `SOCKETIO_ALLOWED_ORIGINS` updated with frontend URL
- [ ] Deployment completed successfully
- [ ] Health check returns `{"status":"UP"}`
- [ ] Swagger UI loads at `/swagger-ui.html`
- [ ] Database migrations ran successfully
- [ ] Login endpoint works
- [ ] Admin endpoints require authentication
- [ ] Frontend can connect to backend
- [ ] Custom domain configured (if applicable)
- [ ] SSL certificate issued (automatic)
- [ ] Monitoring set up
- [ ] Backup strategy defined

---

## 🎉 Success!

If all checks passed, congratulations! Your Morago Backend is now **live on Railway**!

### Your URLs:
- **API**: `https://your-app.up.railway.app`
- **Swagger**: `https://your-app.up.railway.app/swagger-ui.html`
- **Health**: `https://your-app.up.railway.app/actuator/health`

### Next Steps:
1. ✅ Update frontend configuration
2. ✅ Test all features in production
3. ✅ Monitor Railway dashboard
4. ✅ Set up error tracking (optional - Sentry, Datadog)
5. ✅ Configure database backups
6. ✅ Add team members to Railway project

---

## 📞 Need Help?

### Quick Fixes:
1. **Check logs** - Most issues show up in logs
2. **Verify environment variables** - Common source of errors
3. **Test locally** - Ensure it works with `prod` profile
4. **Restart deployment** - Sometimes fixes transient issues

### Get Support:
- Railway Discord: [discord.gg/railway](https://discord.gg/railway)
- Railway Docs: [docs.railway.app](https://docs.railway.app)
- GitHub Issues: For application-specific problems

---

**Happy deploying! 🚀**

*If you found this guide helpful, consider starring the repository!*

