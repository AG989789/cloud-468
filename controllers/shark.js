const Shark = require('../models/sharks');

exports.getSharks = async (req, res) => {
  const sharks = await Shark.find();

  let sharkHTML = sharks.map(s => `<p>${s.name}</p>`).join('');

  res.send(`
    <h1>Shark Info - Alex Giacoio</h1>
    <form method="POST" action="/sharks">
      <input name="name" required />
      <button type="submit">Add Shark</button>
    </form>
    ${sharkHTML}
  `);
};

exports.addShark = async (req, res) => {
  await Shark.create({ name: req.body.name });
  res.redirect('/');
};
