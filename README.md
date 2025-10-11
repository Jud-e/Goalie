# Goalie
Software Engineering Project


## How to Access Project

To switch to a specific branch in the repository, follow these steps:

1. **Clone the Repository** (if you haven't already):

   ```bash
   git clone https://github.com/Jud-e/Goalie.git
   ```
   &emsp; or

   ```bash
   gh repo clone Jud-e/Goalie.git
   ```

2. **Navigate to the Project Directory**:

   ```bash
   cd Goalie
   ```
3. **Create a new Branch and switch to the branch**
    ```bash
   git checkout -b <branch-name/feature-name>
   ```

### Steps to Work on Project Locally

1. **Create and Switch to Your Branch**
     ```bash
     git checkout -b <branch-name/feature-name>
     ```
2. **Pull Latest Changes from Development**
   - Fetch the latest updates from the `Development` branch:
     ```bash
     git fetch origin Developement
     ```
   - Merge the latest changes from `Development` into your branch:
     ```bash
     git merge origin/Development
     ```

3. **Resolve Any Conflicts**
   - If there are any merge conflicts, resolve them in your local environment and commit the changes.

4. **Push Updated Branch**
   - After ensuring everything is working, push your updated branch:
     ```bash
     git push origin <ranch-name/feature-name>
     ```

### Additional Tips
- Regularly check for updates in the `main` branch and repeat the steps above if there are any new changes.
- Always test your code after merging to make sure everything is functioning as expected.
- Reach out to the team if you encounter issues during the merge process.


---
