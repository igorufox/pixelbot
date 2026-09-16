UseItem = new function() {
};
UseItem.prototype = {};

UseItem.params = {
	'active' : true,
	'config' : function(params) {
		return [ {
			name : 'item',
			label : 'Ислользуемая вещь',
			type : 'list',
			values : PixelBot.get_elements("интерфейс.Рюкзак.Квесты")
		} ];
	},
	name : 'Использовать вещь'
}

UseItem.main = function(params) {
	while (true) {
		PixelBot.log.info("Ищем " + params['item']);
		PixelBot.grab_find_click("интерфейс.Рюкзак.Квесты." + params['item'], 1);
		PixelBot.statistics.add("Использовать." + params['item'], 1);
		PixelBot.sleep(1000);
		PixelBot.grab_find_click("интерфейс.Кнопки.Выполнить", 1);
		PixelBot.sleep(1000);
		PixelBot.move_rel(20, 20);
	}
}