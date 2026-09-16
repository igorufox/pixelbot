Battle = new function() {
};

Battle.prototype = {};

Battle.params = {
	'active' : true,
	'config' : function(params) {
		return [ {
			name : 'general',
			label : 'Общее',
			type : 'group',
			values : [ {
				name : 'race',
				label : 'Раса',
				type : 'radio',
				values : [ 'люди', 'магмары' ]
			} ]
		}, {
			name : 'fight',
			label : 'Параметры боя',
			type : 'group',
			values : Fight.params.config(params['fight'])

		} ];
	},
	'default' : {
		'general' : {
			'race' : 'люди'
		}
	},
	name : 'Великие битвы'

}

Battle.main = function(params) {
	PixelBot.browser.detect();
	while (true) {
		PixelBot.state.update();
		var alive = PixelBot.state.health.cur > 0;
		if (alive) {
			Travel.to(params.general.race, "Плато безмолвия", true, null,
					params);
			if (Tools.enterIn('Бои', 'интерфейс.Бои.список боев')) {
				if (PixelBot.find_click('интерфейс.Бои.напасть', 1)) {
					for (var i = 0; i < 5; ++i) {
						if (Fight.check(params.fight)) {
							PixelBot.log.info('batlr alive3' +alive);
							break;
						} else if (PixelBot.grab_find_click('интерфейс.Бои.В бой', 1)) {
							i = 0;
						} else if (PixelBot
								.find('интерфейс.Сообщения.Вы являетесь призраком')) {
							PixelBot.grab_find_click("интерфейс.Кнопки.Закрыть",
									1);
							break;
						} else if (PixelBot
								.find('интерфейс.Сообщения.На эту цель нельзя напасть')) {
							PixelBot.grab_find_click("интерфейс.Кнопки.Закрыть",
									1);
							break;
						}
						PixelBot.sleep(2000);
					}
				}
			}
		}
		PixelBot.state.update();
		alive = PixelBot.state.health.cur > 0;
		if (!alive) {
			Travel.enterInLocation(true, params);
			PixelBot.sleep(5000);
			if (PixelBot.grab_find('интерфейс.Сообщения.Желаете воскреснуть')) {
				if (!PixelBot.find_click('интерфейс.Кнопки.Да', 1)) {
					PixelBot.find_click('интерфейс.Кнопки.В столице', 1)
				}
				PixelBot.sleep(10000);
				PixelBot.grab_find_click("интерфейс.Кнопки.Закрыть", 1);
			}
			Fight.after(params.fight);
		}
	}

}
