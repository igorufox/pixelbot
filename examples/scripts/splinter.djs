Splinter = new function() {
};
Splinter.prototype = {};
Splinter.params = {
	'active' : true,
	'config' : function(params) {
		return [ {
			name : 'server',
			label : 'Сервер',
			type : 'radio',
			values : [ 'w1', 'w2', 'w3', 'w4' ]
		}, {
			name : 'race',
			label : 'Регион',
			type : 'radio',
			values : [ 'люди', 'магмары', 'подводный мир', 'туманные острова' ]
		} ];
	},
	'defaults' : {},
	name : 'Лечить занозу'
}

Splinter.main = function(params) {
	PixelBot.browser.detect();
	var d = PixelBot.browser.get();

	var counter = 0;
	while (true) {
		var location = Travel.detect_curent_location(params['race']);

		var t = PixelBot.link.splinter.response(params['server'], params['race'],
				location);
		if (t != null) {
			PixelBot.log.info('Идем лечить ' + t['nick'] + ' в локациию: '
					+ t['location']);
			Travel.to(params['race'], t['location']);
			PixelBot.log.info('Лечим ' + t['nick']);
			Tools.selectBackpack('Разное');

			PixelBot.grab_find_click("интерфейс.Рюкзак.Разное.Аптечка", 1);
			var tt = 0;
			while (!PixelBot.grab_find('интерфейс.Кнопки.Выполнить')) {
				if (++tt > 20)
					break;
				PixelBot.sleep(1000);
			}
			PixelBot.move_rel_click(25, -85, 1);
			PixelBot.key.paste(t['nick']);
			PixelBot.find_click('интерфейс.Кнопки.Выполнить', 1);
			PixelBot.sleep(10000);

			if (PixelBot.grab_find("интерфейс.Сообщения.Вы являетесь призраком")) {
				PixelBot.find_click('интерфейс.Кнопки.Закрыть', 1);
				PixelBot.statistics.add('Заноза.' + t['nick'], -1);
				PixelBot.sleep(5000);
				Travel.to_church(params.race);
			} else if (PixelBot
					.grab_find("интерфейс.Сообщения.Вы не можете выполнить действие 'Использоватьаптечку'")) {
				PixelBot.find_click('интерфейс.Кнопки.Закрыть', 1);
				PixelBot.statistics.add('Заноза.' + t['nick'], -1);
				PixelBot.log.warning('пустая аптечка ждем 3 часа');
				PixelBot.sleep(10000000);
			} else {
				PixelBot.statistics.add('Заноза.' + t['nick'], 1);
				PixelBot.find_click('интерфейс.Кнопки.Закрыть', 1);
			}
			PixelBot.link.splinter.update(params['server'], t['nick'])
		}
		if (++counter > 20) {
			counter = 0;
			Tools.enterIn("Аукцион", "интерфейс.Аукцион.Деньги");

		}
		PixelBot.sleep(60000);
	}
	return;

}
