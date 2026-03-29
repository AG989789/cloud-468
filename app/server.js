const express = require('express');
const mongoose = require('mongoose');

const app = express();

mongoose.connect('mongodb://mongo:27017/sharks', {
  useNewUrlParser: true,
  useUnifiedTopology: true
});

const Shark = mongoose.model('Shark', { name: String });

app.get('/', async (req, res) => {
  const sharks = await Shark.find();

  res.send(`
    <h1>Shark Info - Alex Giacoio</h1>
    ${sharks.map(s => `<p>${s.name}</p>`).join('')}
  `);
});

app.listen(3000, () => {
  console.log('Server running on port 3000');
});
