Arena = new function() {
};

Arena.prototype = {};

Arena.params = {
	'config' : function(params) {
		return [ {
			name : 'race',
			label : 'Раса',
			type : 'radio',
			values : [ 'люди', 'магмары' ]
		}, {
			name : 'defend',
			label : 'Защищатся при нападении',
			type : 'check'
		} ];
	},
	'defaults' : {
		'defend' : true,
		'race' : 'магмары',
	},
	name : 'Арена'
}

Arena.main = function(params) {
	PixelBot.browser.detect();
	while (true) {
		Tools.enterIn('Поля битв', 'интерфейс.Поля битв.Заявка');
		while (PixelBot.find('интерфейс.Поля битв.Арена')) {
			PixelBot.find_click('интерфейс.Поля битв.Арена', 1);
			PixelBot.sleep(2000);
			PixelBot.grab();
		}
		PixelBot.log.info('Зашли на арену');
		if (!PixelBot.grab_find_click(
				'интерфейс.Поля битв.Кнопки.Подтвердить участие в сражении', 1)) {
			while (!PixelBot
					.find('интерфейс.Поля битв.Кнопки.Отказаться от участия')) {
				if (PixelBot
						.find_click(
								'интерфейс.Поля битв.Кнопки.Встать в межсерверную очередь',
								1)) {
					PixelBot.log.info('встали в очередь');
				}
				PixelBot.sleep(2000);
				PixelBot.grab();
				if (PixelBot.grab_find('интерфейс.Сообщения.Уже в инсте')) {
					break;
				}
			}
		}

		if (PixelBot.grab_find('интерфейс.Сообщения.Уже в инсте')) {
			PixelBot.find_click('интерфейс.Кнопки.Закрыть', 1);
		} else {
			PixelBot.log.info('стоим ждем СЮДА');

			while (!PixelBot
					.grab_find_click(
							'интерфейс.Поля битв.Кнопки.Подтвердить участие в сражении',
							1)) {
				PixelBot.log.info('ждем ...');
				PixelBot.sleep(25000);
				PixelBot.find_click('интерфейс.Поля битв.Кнопки.Обновить', 1);
				PixelBot.sleep(5000);
			}

			PixelBot.log.info('подтвердили ждем начала');

			while (PixelBot.grab_find_click(
					'интерфейс.Поля битв.Кнопки.Обновить', 1)) {
				PixelBot.log.info('ждем ... ...');
				PixelBot.sleep(15000);
			}
		}

		PixelBot.log.info('на арене');
		if (params['race'] == 'люди') {
			PixelBot.log.info('идем за сферой');
			Travel.to('арена', 'Алтарь магмаров', params.defend);
			PixelBot.log.info('несем  сферу на базу');
			Travel.to('арена', 'Сундук людей', params.defend);
			PixelBot.log.info('сдали сферу');
		} else {
			PixelBot.log.info('идем за сферой');
			Travel.to('арена', 'Алтарь людей', params.defend);
			PixelBot.log.info('несем  сферу на базу');
			Travel.to('арена', 'Сундук магмаров', params.defend);
			PixelBot.log.info('сдали сферу');
		}
		PixelBot.log.info('выйдем на арену');
		Travel.to('арена', 'Арена', params.defend);

		PixelBot.log.info('ждем конца инста');

		while (!PixelBot.grab_find_click('интерфейс.Поля битв.Кнопки.Вернуться',
				1)) {
			Fight.check();
			PixelBot.sleep(5000);
		}
		PixelBot.log.info('Вышли с арены');
	}
}