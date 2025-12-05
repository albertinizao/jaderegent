# Jade Regent Frontend

This React application is built using Vite and Tailwind CSS.
It is integrated into the Maven build lifecycle, which installs a local version of Node.js and NPM.

## How to Run

Because Node.js is installed locally in the `target/node` directory (to avoid requiring a global installation), you cannot run `npm` directly unless you add it to your PATH.

### Option 1: Using the provided local Node.js (Recommended)
You can temporarily add the local node to your PATH in PowerShell:

```powershell
$env:Path = "..\target\node;" + $env:Path
npm run dev
```

### Option 2: Using Maven
You can run frontend tasks via the Maven wrapper from the project root:

```bash
# Install dependencies (if not already done)
./mvnw frontend:install-node-and-npm
./mvnw frontend:npm@install

# Run dev server (note: this might not be interactive)
./mvnw frontend:npm@run-dev
```

### Option 3: Global Node.js
If you have Node.js installed globally on your machine, you can simply run:

```bash
npm install
npm run dev
```
