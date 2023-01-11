fs = require('fs');
_ = require('lodash');

const args = process.argv.slice(2);

if (args.length === 1) {
  fs.readFile(args[0], 'utf8', function (err, data) {
    if (err) {
      console.log("Error reading file " + args[0]);
      return;
    }
    const org = JSON.parse(data);
    const map = _.groupBy(org, (city) => city['regiao-imediata']['regiao-intermediaria'].UF.sigla);
    const mapped = Object.fromEntries(_.sortBy(_.map(map, ((value, key) => [key, value.map((info) => info.nome)]))));
    mapped[''] = [];
    console.log(JSON.stringify(mapped));
  });
}


