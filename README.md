# Portfolio Valuation Tool

A command line application to value stocks and cryptocurrencies in your personal portfolio.

## Features

- Valuate stocks and cryptocurrencies based on current market prices
- Store portfolio data in a JSON file for easy access and manipulation
- Store portfolio data in rocksdb for persistence
- Calculate total portfolio value based on current prices
- Display detailed information for individual assets

## Installation

1. Install Java 17 or higher
2. Clone the repository to your local machine.
3. ```./gradlew run --args=-h```

## Usage

Run the portfolio manager node when not passing in any arguments
1. Add assets to your portfolio using the `add` command.
2. View all assets in your portfolio using the `view` command.
3. View detailed information for a specific asset using the `info` command.
4. Remove assets from your portfolio using the `remove` command.
5. Calculate the total value of your portfolio using the `total` command.

### Market Data
Run the `marketdata` subcommands to get information pertaining to market data

## Contributing

Contributions are always welcome! If you have any suggestions or find a bug, please open an issue or submit a pull request.

## License

This project is licensed under the APACHE License - see the [license](./LICENSE) file for details.