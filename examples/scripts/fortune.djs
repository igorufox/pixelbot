Fortune = new function() {
};

Fortune.prototype = {};

Fortune.params = {
	'active' : true,
	'config' : function(params) {
		return [ {
			name : 'race',
			label : 'Раса',
			type : 'radio',
			values : [ 'люди', 'магмары' ]
		}, {
			name : 'mode',
			label : 'Режим',
			type : 'radio',
			values : [ 'Играть', 'Сдавать' ]
		}, {
			name : 'bet',
			label : 'На сколько стекл играть?',
			type : params['mode'] == 'Играть' ? 'radio' : 'hidden',
			values : [ '5', '25', '50' ]
		}, {
			name : 'limit',
			label : 'Добирать если набрано меньше',
			type : params['mode'] == 'Играть' ? 'list' : 'hidden',
			values : [ '5', '6', '7', '8', '9', '10', '11', '12', '13', '14' ]
		}, {
			name : 'timer',
			label : 'Время между кликами',
			type : 'text'
		} ];
	},
	'defaults' : function() {
		return {
			'limit' : '12',
			'timer' : '1500'
		}
	},
	'name' : 'Ловцы фортуны'
}
Fortune.main = function(params) {
	PixelBot.browser.detect();
	if (params['mode'] == 'Играть') {
		while (true) {
			PixelBot.sleep(params.timer);
			if (PixelBot.grab_find_click("фортуна.продолжаем_игру", 1)) {
				PixelBot.log.info("Партия сыграна");
				PixelBot.sleep(params.timer);
			}
			if (params.race == 'люди') {
				if (PixelBot.grab_find_click("фортуна.Катала", 1)) {
					PixelBot.log.info("Привет Катала, я хочу поиграть");
					PixelBot.sleep(params.timer);
				}
			} else {
				if (PixelBot.grab_find_click("фортуна.Покер", 1)) {
					PixelBot.log.info("Привет Покер, я хочу поиграть");
					PixelBot.sleep(params.timer);
				}
			}
			if (PixelBot.grab_find_click("фортуна.игра_дуэль", 1)) {
				PixelBot.log.info("Сыграем в Дуэль");
				PixelBot.sleep(params.timer);
			}

			if (PixelBot.grab_find_click("фортуна." + params.bet + "_стекол", 1)) {
				PixelBot.log.info("Делаем ставку на " + params.bet + " стекол")
			} else {
				// continue;
			}
			var j = 10;
			while (--j > 0) {
				// PixelBot.log.trace("" + j);
				PixelBot.sleep(params.timer);
				PixelBot.grab();
				var points = params.limit;

				for ( var i = 4; i < params.limit; ++i) {
					if (PixelBot.find("фортуна.набрано_" + i + "_очков")) {
						PixelBot.log.info("Набрано " + i + " очка. Берем еще!");
						PixelBot.find_click("фортуна.взять_карту", 1);
						points = i;
						break;
					}
				}

				if (PixelBot.find_click("фортуна.взяли_карту", 1)) {
					PixelBot.log.info("Посчитаем количество очков");
					points = params.limit;
				}
				if (points >= params.limit) {
					if (PixelBot.find_click("фортуна.отдать_ход", 1)) {
						PixelBot.log.info("Мне хватит бери ты "
								+ (params.race == 'люди' ? "Катала" : "Покер"));
						break;
					}
				}
			}
		}
	} else {

		while (true) {
			PixelBot.sleep(params.timer);
			if (PixelBot.grab_find_click("фортуна.продолжаем_игру", 1)) {
				PixelBot.log.info("Партия сдана");
			} else if (PixelBot.grab_find_click("фортуна.Катала", 1)) {
				PixelBot.log.info("Привет Катала, я хочу сдать стекла");

			} else if (PixelBot.grab_find_click("фортуна.Покер", 1)) {
				PixelBot.log.info("Привет Покер, я хочу сдать стекла");

			} else if (PixelBot.grab_find_click("фортуна.Отличная мысль", 1)) {
				PixelBot.log.info("Отличная мысль")
			} else if (PixelBot.grab_find_click("фортуна.100 дивных", 1)) {
				PixelBot.log.info("Сдадим стекла");
			}
		}
	}
}
