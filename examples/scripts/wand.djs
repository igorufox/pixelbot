Wand = new function() {};

Wand.prototype = {};

Wand.params = {
	'config' : function(params) {
		return [ {
			name : 'type',
			label : 'тип',
			type : 'radio',
			values : [ 'люди', 'магмары' ]
		}, {
			name : 'config_type',
			label : 'Тип файл конфигурации',
			type : 'radio',
			values : [ 'cfg', 'xlsx' ]
		}, {
			name : 'config_file',
			label : 'Файл конфигурации',
			type : 'file'
		} ];
	},
	'defaults' : {
		type : 'люди',
		config_type : 'xlsx',
		config_file : 'private\pixelbot.xlsx'
	},
	name : 'Волшебная палочка'
}

Wand.main = function(params) {
	var config = params.config_type == 'cfg' ? Helper.loadConfiguration(params['config_file']) : Helper.loadExcel(params['config_file']);

	for ( var i = 0; i < config.Magmars.length; ++i) {
		var row = config.Magmars[i];
		if (row.wand == "1") {
			PixelBot.log.info(row['nick']);

			while (PixelBot.grab_find_click("интерфейс.Логин.Выход", 1)) {
				PixelBot.log.info("Выйдем из игры");
				PixelBot.sleep(2000);
				PixelBot.key.click(PixelBot.key.VK_ENTER, 0);
				PixelBot.sleep(5000);
			}

			if (PixelBot.grab_find("интерфейс.Логин.e-mail")) {
				PixelBot.move_rel_click(100, 0, 1);
				PixelBot.sleep(1000);
				PixelBot.move_rel_click(100, 0, 1);
				PixelBot.key.click(PixelBot.key.VK_A, PixelBot.key.CTRL_MASK);
				PixelBot.key.click(PixelBot.key.VK_BACK_SPACE, 0);
				PixelBot.key.print(row.login);
			}

			if (PixelBot.grab_find("интерфейс.Логин.пароль")) {
				PixelBot.move_rel_click(100, 0, 1);
				PixelBot.sleep(1000);
				PixelBot.move_rel_click(100, 0, 1);
				PixelBot.key.click(PixelBot.key.VK_A, PixelBot.key.CTRL_MASK);
				PixelBot.key.click(PixelBot.key.VK_BACK_SPACE, 0);
				PixelBot.key.print(row.password);
			}

			PixelBot.sleep(2000);
			PixelBot.key.click(PixelBot.key.VK_ENTER, 0);
			PixelBot.log.info("Зашли в игру: " + row.nick);
			PixelBot.sleep(5000);
		}
	}
}
