const { readdirSync } = require('fs');
const { execSync } = require('child_process');

const CONFIG = '-config.yaml';
const SECRET = '-secret.yaml';
const NAMESPACE = 'adsite';

const dirs = readdirSync('./', { withFileTypes: true })
  .filter(dirent => dirent.isDirectory())
  .map(dirent => dirent.name);

dirs.forEach(dir => {
  readdirSync(`./${dir}`, { withFileTypes: true })
    .filter(dirent => !dirent.isDirectory())
    .map(dirent => dirent.name)
    .filter(file => !file.endsWith(CONFIG) && !file.endsWith(SECRET))
    .forEach(file => console.log(execSync(`kubectl -n ${NAMESPACE} delete -f ${dir}/${file}`).toString()));
});

dirs.forEach(dir => {
  readdirSync(`./${dir}`, { withFileTypes: true })
    .filter(dirent => !dirent.isDirectory())
    .map(dirent => dirent.name)
    .filter(file => file.endsWith(CONFIG))
    .forEach(file => console.log(execSync(`kubectl -n ${NAMESPACE} delete -f ${dir}/${file}`).toString()));
});

dirs.forEach(dir => {
  readdirSync(`./${dir}`, { withFileTypes: true })
    .filter(dirent => !dirent.isDirectory())
    .map(dirent => dirent.name)
    .filter(file => file.endsWith(SECRET))
    .forEach(file => console.log(execSync(`kubectl -n ${NAMESPACE} delete -f ${dir}/${file}`).toString()));
});

console.log(execSync(`kubectl delete ns ${NAMESPACE}`).toString());
