Processing = new function() {
};
Processing.prototype = {};
Processing.params = {
	'active' : true,
	'config' : function(params) {
		return [ {
			name : 'type',
			label : 'Профессия',
			type : 'radio',
			values : [ 'алхимик', 'колдун', 'ювелир' ]
		}, {
			name : 'recipes',
			label : 'Рецепты',
			type : 'selector',
			values : PixelBot.get_elements("рецепты." + params['type'])
		} ];
	},
	'defaults' : {
		'type' : 'алхимик'
	},
	name : 'Перерабатывать'
}

Processing.main = function(params) {
	PixelBot.browser.detect();
	var d = PixelBot.browser.get();

	while (true) {
		var has = false;
		for ( var i = 0; i < params.recipes.length; ++i) {
			PixelBot.grab();
			PixelBot.log.info(params.recipes[i]);
			if (PixelBot.find("рецепты." + params['type'] + '.'
					+ params.recipes[i])) {
				if (PixelBot.find_click_rect('интерфейс.Кнопки.Создать', 1,
						PixelBot.last_find_rect.x + PixelBot.last_find_rect.width,
						PixelBot.last_find_rect.y - 10, d.width
								- PixelBot.last_find_rect.x
								- PixelBot.last_find_rect.width, 25)) {
					PixelBot.statistics.add(params['type'] + "."
							+ params.recipes[i], 1);
					PixelBot.sleep(2000);
					has = true;
				}
			}
		}
		if (!has) {
			PixelBot.sleep(5000);
		}
		PixelBot.grab();
	}
}
