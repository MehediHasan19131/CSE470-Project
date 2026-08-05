# GitHub Push Guide

## Recommended Files To Push

- `pom.xml`
- `docker-compose.yml`
- `README.md`
- `.gitignore`
- `src/`
- `sql/`
- `docs/`

## Files Not To Push

These are already ignored:

- `target/`
- `.env`
- `.venv/`
- `.DS_Store`
- IDE folders such as `.idea/` and `.vscode/`
- log files

## First Push

```bash
git init
git add .
git commit -m "Initial SmartCare Spring MVC project"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPOSITORY.git
git push -u origin main
```

Replace `YOUR_USERNAME` and `YOUR_REPOSITORY` with your GitHub details.
