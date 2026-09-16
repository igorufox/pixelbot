HHTools = new function() {
};

HHTools.prototype = {};

// HHTools.last_hunting_side = 0;
HHTools.exit_arrow_position = null;
HHTools.hunt_area = {};

HHTools.params = {
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
							},
							{
								name : 'resurrect_place',
								label : 'Воскрешаться',
								type : 'radio',
								values : [ 'в ближайшем храме', 'на площади' ]
							},
							{
								name : 'defend',
								label : 'Защищатся при нападении',
								type : 'check'
							},
							{
								name : 'defend_help',
								label : 'При нападении просить помощи',
								type : 'check'
							},
							{
								name : 'defend_help_text',
								label : 'Просьбы помощи при нападаении',
								type : params['general']
										&& params['general']['defend_help'] ? 'user_list'
										: 'hidden'
							},
							{
								name : 'defend_help_target',
								label : 'Канал просьбы помощи',
								type : params['general']
										&& params['general']['defend_help'] ? 'list'
										: 'hidden',
								values : params['general'] ? PixelBot
										.get_elements('интерфейс.Чат.каналы.А')
										: []
							},
							{
								name : 'interfere',
								label : 'Добывать ресурс если уже занят',
								type : 'radio',
								values : [
										'Отменять добычу если начали не первым',
										'Не начинать добычу если ресурс занят',
										'Начинать добычу если ресурс занят' ]
							} ]
				},
				{
					name : 'fight',
					label : 'Параметры боя',
					type : params['general'] && params['general']['defend'] ? 'group'
							: 'hidden',
					values : Fight.params.config(params['fight'])

				},
				{
					name : 'injury',
					label : 'Действия при отсутствии инструмента',
					type : 'group',
					values : [
							{
								name : 'warn_injury',
								label : 'Издавать звук',
								type : 'check'
							},
							{
								name : 'stop_work',
								label : 'Останавливать работу',
								type : 'check'
							},
							{
								name : 'travel_to_city',
								label : 'Идти на цп',
								type : 'check',
								disabled : !params['injury']
										|| params['injury']['stop_work']
							},
							{
								name : 'pickup_tool',
								label : 'Одевать инструмент',
								type : 'check',
								disabled : !params['injury']
										|| params['injury']['stop_work']
							},
							{
								name : 'repair_tool',
								label : 'Чинить инструмент',
								type : 'check',
								disabled : !params['injury']
										|| params['injury']['stop_work']
										|| !params['injury']['pickup_tool']
							},
							{
								name : 'ask_to_heal',
								label : 'Просить вылечить занозу',
								type : 'check',
								disabled : !params['injury']
										|| params['injury']['stop_work']
										|| !params['injury']['pickup_tool']
							},
							{
								name : 'ask_to_heal_text',
								label : 'Просьбы вылечить занозу',
								type : params['injury']
										&& !params['injury']['stop_work']
										&& params['injury']['pickup_tool']
										&& params['injury']['ask_to_heal'] ? 'user_list'
										: 'hidden'
							},
							{
								name : 'ask_to_heal_target',
								label : 'Канал просьбы вылечить занозу',
								type : params['injury']
										&& !params['injury']['stop_work']
										&& params['injury']['pickup_tool']
										&& params['injury']['ask_to_heal'] ? 'list'
										: 'hidden',
								values : params['injury'] ? PixelBot
										.get_elements('интерфейс.Чат.каналы.А')
										: []
							},
							{
								name : 'heal_burn',
								label : 'Лечить ожог',
								type : 'check',
								disabled : !params['injury']
										|| params['injury']['stop_work']
										|| !params['injury']['pickup_tool']

							},
							{
								name : 'report_injury',
								label : 'Сообщать о занозе',
								type : 'check',
							},
							{
								name : 'nickname',
								label : 'Ник персонажа',
								type : params['injury']
										&& !params['injury']['stop_work']
										&& params['injury']['pickup_tool']
										&& (params['injury']['heal_burn'] || params['injury']['report_injury']) ? 'text'
										: 'hidden'
							},
							{
								name : 'server',
								label : 'Сервер',
								type : params['injury']
										&& !params['injury']['stop_work']
										&& params['injury']['pickup_tool']
										&& params['injury']['report_injury'] ? 'radio'
										: 'hidden',
								values : [ 'w1', 'w2', 'w3', 'w4' ]
							} ]
				},
				{
					name : 'location',
					label : 'Место добычи ресурса',
					type : 'group',
					values : [
							{
								name : 'travel',
								label : 'Следовать к месту добычи ресурса',
								type : 'check'
							},
							{
								name : 'travel_target',
								label : 'Место добычи ресурса',
								type : params['location']
										&& params['location']['travel'] ? 'list'
										: 'hidden',
								values : params['general'] ? PixelBot
										.get_elements("локации."
												+ params['general']['race']
												+ ".тексты") : []
							} ]
				} ];
	},
	'default' : function() {
		return {
			'general' : {
				'race' : 'люди',
				'defend' : false,
				'interfere' : 'Отменять добычу если начали не первым',
				'travel' : false
			}
		}
	}
}

HHTools.main2 = function(params) {
	PixelBot.browser.detect();
	this.exit_arrow_position = null;
	// this.scroll_top_position = null;
	while (true) {
		// this.change_side();
		this.scroll()
		PixelBot.sleep(1000);
	}
}

HHTools.change_side = function(side) {
	var r = true;
	while (this.exit_arrow_position == null) {
		r = this.init();
		if (!r) {
			HHTools.enterInHunt();
		}
	}
	if (r) {
		if (side == null || typeof side == 'undefined') {
			side = Math.floor(Math.random() * 4);
			if (side >= this.last_hunting_side) {
				++side;
			}
			side %= 4
			if (side >= this.last_hunting_side) {
				++side;
			}
		}
		this.last_hunting_side = side;

		switch (side) {
		case 0:
			PixelBot.move_click(this.exit_arrow_position.x - 450,
					this.exit_arrow_position.y + 5, 1)
			break;
		case 1:
			PixelBot.move_click(this.exit_arrow_position.x - 375,
					this.exit_arrow_position.y + 5, 1)
			break;
		case 2:
			PixelBot.move_click(this.exit_arrow_position.x - 450,
					this.exit_arrow_position.y + 30, 1)
			break;
		case 3:
			PixelBot.move_click(this.exit_arrow_position.x - 375,
					this.exit_arrow_position.y + 30, 1)
			break;
		}
	}
}

HHTools.init = function() {
	this.exit_arrow_position = null;
	// this.scroll_top_position = null;
	var d = PixelBot.browser.get();
	PixelBot.log.info('Заходим в охоту');
	PixelBot.grab_find_click('интерфейс.Охота.закрыть диалог', 1);
	if (PixelBot.grab_find_rect("интерфейс.Охота.стрелка выхода", 880, 80,
			d.width - 880, 40)) {
		this.exit_arrow_position = PixelBot.last_find_rect;

		PixelBot.grab_find_rect("интерфейс.Охота.стрелка скрола",
				this.exit_arrow_position.x - 30,
				this.exit_arrow_position.y + 45, d.width
						- this.exit_arrow_position.x + 30, 30);
		// this.scroll_top_position = PixelBot.last_find_rect;
		this.hunt_area.x = 70;
		this.hunt_area.y = this.exit_arrow_position.y + 45;
		this.hunt_area.width = PixelBot.last_find_rect.x - this.hunt_area.x + 15;

		this.hunt_area.height = PixelBot.grab_square(0xFFFFAA, 0xFFFFAA,
				this.hunt_area.x + this.hunt_area.width - 10, this.hunt_area.y,
				15, d.height - this.hunt_area.y);
		// PixelBot.grab_rect(this.hunt_area.x, this.hunt_area.y,
		// this.hunt_area.width, this.hunt_area.height);

		return true;
	}
	return false;
}

HHTools.getHuntArea = function() {
	return Helper.getRectangle(this.hunt_area.x, this.hunt_area.y,
			this.hunt_area.width, this.hunt_area.height);
}

HHTools.scroll = function(callback) {
	while (this.exit_arrow_position == null) {
		if (!this.init()) {
			this.enterInHunt();
		}
	}
	var d = PixelBot.browser.get();

	var y = Math.floor(1500 / this.hunt_area.height) + 1;
	var step = Math.floor(this.hunt_area.height / y);

	this.scrollx_to('l');
	for (var j = 0; j < y; ++j) {
		this.scrolly_to(j * step);
		if (!callback(this.hunt_area.x, this.hunt_area.y, this.hunt_area.width,
				this.hunt_area.height, 'l' + (j * step))) {
			return false;
		}
	}
	this.scrollx_to('r');
	for (var j = y - 1; j >= 0; --j) {
		this.scrolly_to(j * step);
		if (!callback(this.hunt_area.x, this.hunt_area.y, this.hunt_area.width,
				this.hunt_area.height, 'r' + (j * step))) {
			return false;
		}
	}
	return true;
}

HHTools.scroll_to = function(pos) {
	PixelBot.log.trace('scroll to ' + pos);
	HHTools.scrollx_to(pos[0]);
	HHTools.scrolly_to(parseInt(pos.substring(1)));
}

HHTools.scrollx_to = function(posx) {
	// PixelBot.log.trace('scroll X to ' + posx);
	var bottom = this.hunt_area.y + this.hunt_area.height - 15;
	var side = this.hunt_area.x + this.hunt_area.width - 10;
	var s = 0;
	var i = 0;

	PixelBot.grab_rect(this.hunt_area.x, bottom, this.hunt_area.width, 1);
	for (i = this.hunt_area.x; i < side; ++i) {
		var p = PixelBot.screen.get_pixel(i, bottom);
		if (0xFFFFE2A8 - 0x100000000 == p) {
			++s;
		} else {
			s = 0;
		}
		if (s == 10)
			break;
	}
	// PixelBot.log.trace('scroll X1 to ' + i);
	PixelBot.mouse.move(i, bottom);
	PixelBot.mouse.press(1);
	if (posx == 'l') {
		PixelBot.mouse.move(this.hunt_area.x, bottom);
	} else {
		PixelBot.mouse.move(this.hunt_area.x + this.hunt_area.width, bottom);
	}
	PixelBot.mouse.release(1);
	PixelBot.sleep(50);
}

HHTools.scrolly_to = function(posy) {
	// PixelBot.log.trace('scroll Y to ' + posy);
	var bottom = this.hunt_area.y + this.hunt_area.height - 15;
	var side = this.hunt_area.x + this.hunt_area.width - 10;
	var s = 0;
	var i = 0;

	PixelBot.grab_rect(side, this.hunt_area.y, 1, this.hunt_area.height);
	for (i = this.hunt_area.y; i < bottom; ++i) {
		var p = PixelBot.screen.get_pixel(side, i);
		if (0xFFFFE2A8 - 0x100000000 == p) {
			++s;
		} else {
			s = 0;
		}
		if (s == 10)
			break;
	}
	// PixelBot.log.trace('scroll Y1 to ' + i);
	PixelBot.mouse.move(side, i);
	PixelBot.mouse.press(1);
	PixelBot.mouse.move(side, this.hunt_area.y + posy);
	PixelBot.mouse.release(1);
	PixelBot.sleep(50);

}

// HHTools.getHuntArea = function() {
// var d = PixelBot.browser.get();
// var h = PixelBot.grab_square(0xFFFFAA, 0xFFFFAA, this.exit_arrow_position.x -
// 45, this.exit_arrow_position.y, 45, d.height - this.exit_arrow_position.y);
//
// return Helper.getRectangle(this.scroll_top_position.x - 760,
// this.scroll_top_position.y - 5, 760, h + 10)
// }

HHTools.enterInHunt = function() {
	if (Tools.enterIn('Охота', 'интерфейс.Охота.стрелка выхода')) {
		return this.init();
	}
	return false;
}

HHTools.loop = function(callback) {
	while (true) {
		// this.change_side();
		// PixelBot.sleep(100);
		this.scroll(callback);
	}
}

HHTools.fight = function(params) {
	var r = true;
	var d = PixelBot.browser.get();
	if (!PixelBot.grab_find_rect("интерфейс.Охота.стрелка выхода", 840, 80,
			d.width - 840, 40)) {
		this.exit_arrow_position = null;
		r = false;
		PixelBot.log.info('на нас напали?');
		for (var k = 0; k < 5; ++k) {
			PixelBot.sleep(5000);
			if (PixelBot.grab_find_rect("интерфейс.Бой.Vs", 0, 80, d.width, 50)) {
				var vs_location = PixelBot.last_find_rect;
				PixelBot.log.info('точно напали');
				if (params['general']['defend_help']) {
					var texts = params['general']['defend_help_text'];
					if (texts.length > 0) {
						var text = texts[Math.round((texts.length - 1)
								* Math.random())];
						Tools.sayText(text,
								params['general']['defend_help_target']);
					}
				}

				var alive = true;
				if (params.general.defend) {
					PixelBot.log.info('проводим бой');
					Fight.vs_location = vs_location;
					alive = Fight.process(params.fight);
				} else {
					PixelBot.log.info('ждем пока убьют');
					while (true) {
						if (PixelBot.grab_find_rect('интерфейс.Бой.Победа',
								vs_location.x - 100, vs_location.y + 50, 200,
								250)) {
							PixelBot.statistics.add("Бой", +1);
							HHTools.enterInHunt();
							break;
						} else if (PixelBot.find_rect('интерфейс.Бой.Поражение',
								vs_location.x - 100, vs_location.y + 50, 200,
								250)) {
							alive = false;
							PixelBot.statistics.add("Бой", -1);
							PixelBot.log.info('убили');
							break;
						} else if (PixelBot.find_rect(
								'интерфейс.Бой.Покинуть бой',
								vs_location.x - 100, vs_location.y + 50, 200,
								250)) {
							alive = false;
							PixelBot.statistics.add("Бой", -1);
							PixelBot.log.info('Можем выйти из боя');
							break;
						}
						PixelBot.sleep(3000);
					}
				}
				if (!alive) {
					Travel.to_church(params.general.race,
							params.general.resurrect_place == 'на площади' ? 1
									: 0);
				}
				break;
			}
		}
		HHTools.enterInHunt();
	}
	return r;
}

HHTools.gather = function(params, x, y, width, height, type, item) {
	var r = true;
	PixelBot.grab_rect(x, 0, width, height + y);
	if (PixelBot
			.find_rect('интерфейс.Охота.нет инструмента', x, y, width, height)) {
		if (params['injury']['warn_injury']) {
			PixelBot.sound.play('notify.wav');
		}
		if (params['injury']['stop_work']) {
			throw ('Заноза');
		} else if (params['injury']['pickup_tool']) {
			PixelBot.log.info('Нет инструмента, пробуем одеть');
			this.pickupTool(params);
			if (params['injury']['travel_to_city']) {
				Travel.to(params.general.race, params.location.travel_target,
						params.general.defend);
			}

			this.enterInHunt();
			r = false;
		}
	} else if (PixelBot
			.find_rect('интерфейс.Охота.перегруз', x, y, width, height)) {
		throw ('Перегруз');
	} else if (PixelBot.find_rect('интерфейс.Охота.закрыть игру', x, 0, width,
			height + y)) {
		PixelBot.log.info("CAPTHA 1");
		this.solveCaptcha(params['resource']['type'], x, 0, width, height + y,
				"1");
	} else if (PixelBot.find_rect('интерфейс.Охота.труп', x, y, width, height)) {
		PixelBot.log.info('труп');
		Travel.to_church(params.general.race,
				params.general.resurrect_place == 'на площади' ? 1 : 0);
		if (PixelBot.grab_find('интерфейс.Сообщения.Желаете воскреснуть')) {
			PixelBot.log.info('Не смог воскреситься');
			PixelBot.find_click('интерфейс.Кнопки.Да', 1);
			throw ('Труп');
		}
		HHTools.enterInHunt();
		r = false;
	}
	var success = true;
	while (PixelBot.find_rect('интерфейс.Охота.добыча', x, y, width, height)) {

		if (params['general']['interfere'] == 'Отменять добычу если начали не первым'
				&& PixelBot.find_rect(
						'интерфейс.Охота.Вы начали добывать ресурс не первым',
						x, y, width, height)) {
			PixelBot.find_click_rect('интерфейс.Охота.отменить', 1, x, y, width,
					height)
			success = false;
			break;
		} else if (PixelBot.find_click_rect('интерфейс.Охота.закрыть диалог', 1,
				x, y, width, height)) {
			success = false;
			break;
		} else if (PixelBot.find_rect('интерфейс.Охота.закрыть игру', x, 0,
				width, height + y)) {
			PixelBot.log.info("CAPTHA 2");
			this.solveCaptcha(params['resource']['type'], x, 0, width, height
					+ y, "2");
			break;
		}
		PixelBot.sleep(3000);
		PixelBot.grab_rect(x, y, width, height);
	}
	if (PixelBot.grab_find_rect('интерфейс.Охота.закрыть игру', x, 0, width,
			height + y)) {
		PixelBot.log.info("CAPTHA 3");
		this.solveCaptcha(params['resource']['type'], x, 0, width, height + y,
				"3");
	}
	PixelBot.statistics.add(type + "." + item, success ? +1 : -1);
	return r;
}

HHTools.travel = function(params) {
	if (params.location.travel) {
		Travel.to(params.general.race, params.location.travel_target,
				params.general.defend, null, params);
	}
}

HHTools.pickupTool = function(params) {
	var d = PixelBot.browser.get();

	var tool = ''
	var section = 'Вещи';
	var x = 100;
	var y = 260;

	if (params['resource']['type'] == 'texture') {
		tool = "Серп";
	} else if (params['resource']['type'] == 'tile') {
		tool = "Кирка";
	} else if (params['resource']['type'] == 'edge') {
		tool = "Удочка";
	} else if (params['resource']['type'] == 'relic') {
		tool = "Негаторы";
		// section = 'Разное';
		x = 350;
		y = 190;
	}

	var tool2 = tool;

	do {
		Tools.selectBackpack(section);

		if (!PixelBot.find("интерфейс.Рюкзак.Вместимость"))
			continue;
		var r = PixelBot.last_find_rect;

		var tools = PixelBot.get_elements("интерфейс.Рюкзак." + section + "."
				+ tool);
		if (tools.length == 0) {
			if (PixelBot.grab_find_click_rect("интерфейс.Рюкзак." + section + "."
					+ tool, 1, r.x, r.y, d.width - r.x, d.height - r.y)) {
				tool2 = tool;
			}
		} else {
			for (var ii = tools.length - 1; ii >= 0; --ii) {
				if (PixelBot.grab_find_click_rect("интерфейс.Рюкзак." + section
						+ "." + tool + "." + tools[ii], 1, r.x, r.y, d.width
						- r.x, d.height - r.y)) {
					tool2 = tool + '.' + tools[ii];
					break;
				}
			}
		}
		// PixelBot.grab_find_click_rect("интерфейс.Рюкзак.Вещи." + tool, 1, r.x,
		// r.y, d.width - r.x, d.height - r.y);
		PixelBot.move(r.x, r.y);
		PixelBot.sleep(5000);
		if (PixelBot.grab_find("интерфейс.Сообщения.Вы травмированы")) {
			PixelBot.grab_find_click("интерфейс.Кнопки.Закрыть", 1);
			if (params['injury']['travel_to_city']) {
				if (params['general']['race'] == 'люди') {
					Travel.to(params.general.race, 'Площадь О\'Дельвайса',
							params.general.defend, null, params);
				} else {
					Travel.to(params.general.race, 'Площадь Дартронга',
							params.general.defend, null, params);
				}
			}
			if (params['injury']['report_injury']) {
				var location = Travel
						.detect_curent_location(params['general']['race']);
				PixelBot.link.splinter.request(params['injury']['server'],
						params.general.race, location,
						params['injury']['nickname']);
			}
			if (params['injury']['ask_to_heal']) {
				var texts = params['injury']['ask_to_heal_text'];
				if (texts.length > 0) {
					var text = texts[Math.round((texts.length - 1)
							* Math.random())];
					Tools.sayText(text, params['injury']['ask_to_heal_target']);
				}
			}
			if (params['injury']['heal_burn']) {
				Tools.selectBackpack('Эффекты');
				if (PixelBot.grab_find_click(
						'интерфейс.Рюкзак.Эффекты.Свиток лечения ожогов', 1)) {
					while (!PixelBot.grab_find('интерфейс.Кнопки.Выполнить')) {
						PixelBot.sleep(1000);
					}
					PixelBot.move_rel_click(25, -50, 1);
					PixelBot.log.info(params['injury']['nickname']);
					PixelBot.key.paste(params['injury']['nickname']);
					PixelBot.find_click('интерфейс.Кнопки.Выполнить', 1);
					continue;
				}
			}

			var time = 200000 + Math.round(200000 * Math.random())
			PixelBot.log.info('Травма или заноза - ждем пока пройдет '
					+ Math.round(time / 60000) + 'мин '
					+ Math.round(time / 1000) % 60 + 'сек');
			if (params.general.defend) {
				while (time > 0) {
					if (PixelBot.grab_find_rect("интерфейс.Бой.Vs", 0, 80,
							d.width, 50)) {
						this.fight(params);
					}
					PixelBot.sleep(30000);
					time -= 30000;
				}
			} else {
				PixelBot.sleep(time);
			}
			continue;
		}
		if (PixelBot.grab_find("интерфейс.Сообщения.Вещь без прочности")) {
			if (params['injury'].repair_tool) {
				PixelBot.log.info('Инструмент сломан - идем ремонтировать');
				PixelBot.grab_find_click("интерфейс.Кнопки.Закрыть", 1);
				this.repairTool(params);

				Tools.enterIn("Рюкзак", "интерфейс.Рюкзак.Вместимость");
				while (!PixelBot.grab_find("интерфейс.Рюкзак.Вещи(А)")) {
					PixelBot.grab_find_click("интерфейс.Рюкзак.Вещи(П)", 1);
					PixelBot.sleep(3000);
				}
			} else {
				PixelBot.log.info('Инструмент сломан');
				throw ('Tool broken')
			}
			continue;
		}
		if (PixelBot
				.grab_find("интерфейс.Сообщения.Вы сильно устали и не можете работать")) {
			PixelBot.log.info('Трудоголик');
			throw ('Overwork');
		}

	} while (!PixelBot.grab_find_rect(
			"интерфейс.Рюкзак." + section + "." + tool2, x, y, 60, 60));
}

HHTools.repairTool = function(params) {
	var cur_loc = Travel.detect_curent_location(params.general.race);
	Travel.to(params.general.race, "Магазин", params.general.defend);
	PixelBot.log.info("Пришли в магазин");
	while (!PixelBot.grab_find("интерфейс.Магазин.Мастерская(А)")) {
		PixelBot.grab_find_click("интерфейс.Магазин.Мастерская(П)", 1);
		PixelBot.sleep(3000);
	}
	var tool = ''
	if (params['resource']['type'] == 'texture') {
		tool = "Серп";
	} else if (params['resource']['type'] == 'tile') {
		tool = "Кирка";
	} else if (params['resource']['type'] == 'edge') {
		tool = "Удочка";
	}
	tool += '.серый'

	while (PixelBot.grab_find_click("интерфейс.Рюкзак.Вещи." + tool, 1)) {
		PixelBot.sleep(3000);
	}

	PixelBot.grab_find_click("интерфейс.Магазин.Починить", 1);
	PixelBot.log.info("Починили возвращаемся: " + cur_loc);
	Travel.to(params.general.race, cur_loc, params.general.defend);
}

HHTools.solveCaptcha = function(type, x, y, width, height, param) {
	PixelBot.sound.play('notify.wav');
	if (PixelBot.find_rect('интерфейс.Охота.закрыть игру', x, y, width, height)) {
		var cx = PixelBot.last_find_rect.x - 400;
		var cy = PixelBot.last_find_rect.y + 30;
		var cwidth = 390;
		var cheight = 260;

		// PixelBot.highlight_region(cx, cy, cwidth, cheight);
		PixelBot.log.info("Решаем игру");
		var res = null;
		var t = 0;
		while ((res = PixelBot.solveCaptcha(type, cx, cy, cwidth, cheight, param)) == null) {
			PixelBot.log.error("Ошибка в анализе картинки");
			PixelBot.statistics.add('Прерывания.Мини-игра', -1);
			PixelBot.sleep(3000);
			if (++t > 10) {
				PixelBot.grab_find_click_rect('интерфейс.Кнопки.Готово', 1, x, y,
						width, height);
				return;
			}
		}

		PixelBot.log.info("Решили, собираем картинку");

		var positions = [ [ 60, 60 ], [ 190, 60 ], [ 320, 60 ], [ 60, 190 ],
				[ 190, 190 ], [ 320, 190 ] ];
		for (var i = 0; i < 6; ++i) {
			PixelBot.grab_rect(cx, cy, cwidth, cheight);
			if (PixelBot.find_move_rect(res[i], cx, cy, cwidth, cheight)) {
				PixelBot.log.info("Двигаем часть №" + (i + 1));

				PixelBot.mouse.press(1);
				PixelBot.mouse.move(cx + positions[i][0], cy + positions[i][1]);
				PixelBot.mouse.release(1);
			}
			PixelBot.sleep(1000);

		}
		PixelBot.statistics.add('Прерывания.Мини-игра', +1);

		PixelBot.grab_find_click_rect('интерфейс.Кнопки.Готово', 1, x, y, width,
				height);
		PixelBot.sleep(1000);
	}
}
