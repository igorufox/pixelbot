Grass = new function() {
};
Grass.prototype = {};
Grass.params = {
	'active' : true,
	'config' : function(params) {
		if (!params) {
			params = {};
		}
		var p = HHTools.params.config(params);
		return p
				.concat([ {
					name : 'resource',
					label : 'Ресурс',
					type : 'group',
					values : [
							{
								name : 'type',
								label : 'Тип',
								type : 'radio',
								values : [ 'texture', 'relic', 'tile',
										'кристаллы' ]
							},
							{
								name : 'grass',
								label : 'Травы',
								type : params['resource']
										&& params['resource']['type'] == 'texture' ? 'selector'
										: 'hidden',
								values : PixelBot.get_elements("ресурсы.травы")
							},
							{
								name : 'relicts',
								label : 'Реликты',
								type : params['resource']
										&& params['resource']['type'] == 'relic' ? 'selector'
										: 'hidden',
								values : PixelBot.get_elements("ресурсы.реликты")
							},
							{
								name : 'stones',
								label : 'Камни',
								type : params['resource']
										&& params['resource']['type'] == 'tile' ? 'selector'
										: 'hidden',
								values : PixelBot.get_elements("ресурсы.камни")
							},
							{
								name : 'crystall',
								label : 'Кристаллы',
								type : params['resource']
										&& params['resource']['type'] == 'кристаллы' ? 'selector'
										: 'hidden',
								values : PixelBot
										.get_elements("ресурсы.кристаллы")
							} ]
				} ]);
	},
	'defaults' : function() {
		var p = HHTools.params['default']();
		return p;
	},
	'name' : 'Собирать траву/камни'
}

Grass.main = function(params) {
	PixelBot.browser.detect();
	HHTools.travel(params);
	if (!HHTools.init()) {
		HHTools.enterInHunt();
	} else {
		PixelBot.log.success('Уже в охоте');
	}
	Fight.init(params.fight);
	HHTools.loop(function(x, y, width, height) {
		return Grass.find(params, x, y, width, height);
	});
}

Grass.find = function(params, x, y, width, height) {
	var r = true;
	var resource = null;
	if (params['resource']['type'] == 'texture') {
		resource = params['resource'].grass;
	} else if (params['resource']['type'] == 'relic') {
		resource = params['resource'].relicts;
	} else if (params['resource']['type'] == 'tile') {
		resource = params['resource'].stones;
	} else if (params['resource']['type'] == 'кристаллы') {
		resource = params['resource'].crystall;
	}
	// if (!Object.prototype.toString.call(resource) != "[object Array]") {
	// resource = [ resource ];
	// }

	var defend = params.general.defend;
	var race = params.general.race;
	var res_prefix = '';

	if (params['resource']['type'] == 'texture') {
		res_prefix = 'ресурсы.травы.';
	} else if (params['resource']['type'] == 'relic') {
		res_prefix = 'ресурсы.реликты.';
	} else if (params['resource']['type'] == 'tile') {
		res_prefix = 'ресурсы.камни.';
	} else if (params['resource']['type'] == 'кристаллы') {
		res_prefix = 'ресурсы.кристаллы.';
	}

	var harvest = false;
	do {
		harvest = false;
		PixelBot.grab_rect(x, 0, width, height + y);

		if (PixelBot.find_click_rect('интерфейс.Охота.закрыть диалог', 1, x, y,
				width, height)) {
			PixelBot.grab_rect(x, y, width, height);
		}
		if (PixelBot.find_rect('интерфейс.Охота.закрыть игру', x, 0, width,
				height + y)) {
			PixelBot.log.info("CAPTHA 9");
			HHTools.solveCaptcha(params['resource']['type'], x, 0, width,
					height + y, "9");
		}
		PixelBot.log.trace('Ищем: ' + resource);
		var harvested_resource = '';
		for ( var k = resource.length - 1; k >= 0; --k) {
			var found = PixelBot.find_many_rect(res_prefix + resource[k], x, y,
					width, height);
			PixelBot.log.trace('Обнаружено ' + found.length + ' ' + resource[k]);
			for ( var j = 0; j < found.length && j < 5; ++j) {
				if (params['general']['interfere'] != 'Начинать добычу если ресурс занят'
						&& PixelBot.find_rect('интерфейс.Охота.ресурс занят',
								found[j].x - 20, found[j].y - 20, 20, 20)) {
					PixelBot.log.trace(resource[k] + ' №' + (j + 1) + ' занят');
				} else {
					harvested_resource = resource[k];
					// PixelBot.log.trace('монср №' + (j + 1) + ' свободен');
					PixelBot.move_click(found[j].x, found[j].y, 1);
					PixelBot.sleep(100);
					if (PixelBot
							.grab_find_click_rect(
									((params['resource']['type'] == 'texture' || params['resource']['type'] == 'relic') ? 'интерфейс.Охота.косить'
											: 'интерфейс.Охота.добывать'), 1, x
											+ (width - 750) / 2, y - 45, 75, 50)) {
						PixelBot.log.trace('собираем ' + resource[k] + ' №'
								+ (j + 1));
						harvest = true;
						PixelBot.sleep(2000);
						break;
					}
				}

				// PixelBot.log.trace('Обнаружено ' + found.length + ' ' +
				// monster);
				// PixelBot.highlight_region(found[j].x + found[j].width / 2 - 20,
				// found[j].y - 40, 10, 10);
			}
			if (harvest)
				break;
		}
		if (harvest) {
			HHTools.gather(params, x, y, width, height,
					params['resource']['type'], harvested_resource);
		}
		harvest = (r = HHTools.fight(params)) && harvest;

	} while (harvest)
	return r;
}