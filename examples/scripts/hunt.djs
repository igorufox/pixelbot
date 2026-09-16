Hunt = new function() {
};

Hunt.prototype = {};

Hunt.params = {
	'active' : true,
	'config' : function(params) {
		return [
				{
					name : 'general',
					label : 'Общее',
					type : 'group',
					values : [
							{
								name : 'race',
								label : 'Регион',
								type : 'radio',
								values : [ 'люди', 'магмары', 'подводный мир',
										'туманные острова' ]
							}, {
								name : 'monsters',
								label : 'Цель охоты',
								type : 'selector',
								values : PixelBot.get_elements("монстры")
							}, {
								name : 'interfere',
								label : 'Вмешиваться в бой',
								type : 'check'
							} ]
				},
				{
					name : 'location',
					label : 'Место охоты',
					type : 'group',
					values : [
							{
								name : 'travel',
								label : 'Следовать к месту охоты',
								type : 'check'
							},
							{
								name : 'travel_target',
								label : 'Место охоты',
								type : params['location']
										&& params['location']['travel'] ? 'list'
										: 'hidden',
								values : params['general'] ? PixelBot
										.get_elements("локации."
												+ params['general']['race']
												+ ".тексты") : []
							},
							{
								name : 'enter_in_estate',
								label : 'Заходить в поместье',
								type : params['location']
										&& params['location']['travel'] ? 'check'
										: 'hidden'
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
	name : 'Охотиться'

}

Hunt.main = function(params) {
	PixelBot.browser.detect();
	HHTools.travel(params);
	if (!HHTools.init()) {
		HHTools.enterInHunt();
	} else {
		PixelBot.log.success('Уже в охоте');
	}
	Fight.init(params.fight);
	HHTools.loop(function(x, y, width, height) {
		return Hunt.findMonster(params, x, y, width, height);
	});

}

Hunt.findMonster = function(params, x, y, width, height) {
	var fight = false;
	loop: for (var i = 0; i < params.general.monsters.length; ++i) {
		var monster = params.general.monsters[i];

		PixelBot.grab_rect(x, y, width, height);
		var found = PixelBot.find_many_rect('монстры.' + monster, x, y, width,
				height);
		PixelBot.log.trace('Обнаружено ' + found.length + ' ' + monster);
		for (var j = 0; j < found.length; ++j) {
			if (PixelBot.find_rect('интерфейс.Охота.ресурс занят', found[j].x
					+ found[j].width / 2 - 30, found[j].y - 50, 20, 20)) {
				PixelBot.log.trace('монср №' + (j + 1) + ' занят');
			} else {
				// PixelBot.log.trace('монср №' + (j + 1) + ' свободен');
				PixelBot.move_click(found[j].x + found[j].width / 2,
						found[j].y - 15, 1);
				if (PixelBot.grab_find_click_rect('интерфейс.Охота.атаковать', 1,
						x + (width - 750) / 2, y - 45, 75, 50)) {
					PixelBot.log.trace('напали на  монсра №' + (j + 1));
					PixelBot.sleep(200);
					PixelBot.click(1);
					fight = true;
					break loop;
				}
			}
			// PixelBot.log.trace('Обнаружено ' + found.length + ' ' + monster);
			// PixelBot.highlight_region(found[j].x + found[j].width / 2 - 20,
			// found[j].y - 40, 10, 10);
		}
	}
	if (fight) {
		PixelBot.log.trace('ждем начала боя');
		for (var i = 0; i < 15; ++i) {
			PixelBot.sleep(1000);
			PixelBot.grab();
			if (PixelBot
					.find('интерфейс.Сообщения.Вы пытаетесь напасть на монстра, который уже в бою')) {
				if (params.general.interfere) {
					PixelBot.find_click('интерфейс.Кнопки.ОК', 1)
				} else {
					PixelBot.find_click('интерфейс.Кнопки.ОТМЕНА', 1)
					break;
				}
			}
			if (PixelBot.find('интерфейс.Сообщения.Вы являетесь призраком')) {
				if (PixelBot.find_click('интерфейс.Кнопки.Закрыть', 1)) {
					PixelBot.sleep(1000);
					PixelBot.grab();
					Travel.to_church(params.general.race);
					HHTools.enterInHunt();
				}
				break;
			}
			if (PixelBot.find('интерфейс.Сообщения.Цель еще не востановилась')) {
				PixelBot.log.info('Цель еще не востановилась');
				if (PixelBot.find_click('интерфейс.Кнопки.Закрыть', 1)) {
					PixelBot.sleep(1000);
					PixelBot.grab();
					break;
				}
			}
			if (Fight.check(params.fight)) {
				break;
			}

		}
		if (!HHTools.init()) {
			HHTools.enterInHunt();
		}
	}

	PixelBot.sleep(500);
	return true;
}