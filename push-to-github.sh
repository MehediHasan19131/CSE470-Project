#!/usr/bin/env bash
#
# Publish the SmartCare (CSE470) project to GitHub's MAIN branch (final version).
# Usage:
#   bash push-to-github.sh                 # default commit message
#   bash push-to-github.sh "your message"  # custom commit message
#
set -euo pipefail

# Always operate from the folder this script lives in (the project root)
cd "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
echo "==> Project: $(pwd)"

# 1) Clear any stale git lock left behind by an editor or a crash
rm -f .git/index.lock

# 2) Delete throwaway/junk so it never gets committed
rm -rf _to_delete build-export.tgz changes.tar changes_extract 2>/dev/null || true
find . -name '.DS_Store' -delete 2>/dev/null || true

# 3) Make sure build output & junk are ignored (idempotent)
for ig in 'target/' '.DS_Store' '_to_delete/'; do
  grep -qxF "$ig" .gitignore 2>/dev/null || echo "$ig" >> .gitignore
done
git rm -r --cached --quiet target 2>/dev/null || true

# 4) Stage everything
git add -A

# 5) Commit current work (skip cleanly if nothing changed)
MSG="${1:-Final version: SmartCare healthcare platform (class diagram, payments, ambulance, pharmacy, support modules + docs)}"
if git diff --cached --quiet; then
  echo "==> Nothing new to commit; using existing commits."
else
  git commit -m "$MSG"
fi

# 6) Publish the current snapshot to the MAIN branch on GitHub
LOCAL="$(git rev-parse --abbrev-ref HEAD)"
echo "==> Publishing '$LOCAL' -> origin/main ..."
if git push origin "HEAD:main"; then
  echo "==> DONE. Final version is on main:"
  echo "    https://github.com/MehediHasan19131/CSE470-Project/tree/main"
else
  echo ""
  echo "!! Push to main was REJECTED — remote 'main' has commit(s) your local copy doesn't have."
  echo "   Pick ONE:"
  echo "   A) Merge the remote main in, then re-run this script:"
  echo "        git fetch origin && git merge origin/main    # resolve conflicts if any, then commit"
  echo "   B) Overwrite remote main with THIS as the final version (only if you're sure no one else's work is there):"
  echo "        git push --force origin HEAD:main"
  exit 1
fi
