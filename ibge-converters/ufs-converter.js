fs = require('fs');

const args = process.argv.slice(2);

if (args.length === 1) {
  fs.readFile(args[0], 'utf8', function (err, data) {
    if (err) {
      console.log("Error reading file " + args[0]);
      return;
    }
    const org = JSON.parse(data);
    const mapped = Object.fromEntries(org.map((o) => [o.sigla, o.nome]))
    console.log(JSON.stringify(mapped));
  });
}


