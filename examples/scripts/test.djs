Test = new function() {
};

Test.prototype = {};

Test.params = {
	'config' : function(params) {
		return [ {
			name : 'type',
			label : 'Профессия',
			type : 'list',
			values : PixelBot.get_elements("ресурсы.травы")
		}, {
			name : 'fight',
			label : 'Параметры боя',
			type : 'group',
			values : Fight.params.config(params['fight'])

		} ];
	}
}

Test.main = function(params) {

	PixelBot.log.info('1: ');
	PixelBot.sound.play('notify.wav');
	PixelBot.log.info('2: ');
	return

	PixelBot.browser.detect();
	Travel.to_church('люди');
	// Tools.sayText ('text', 'Группа');
	return 

	PixelBot.statistics.add("type1.item1", 1);
	PixelBot.sleep(3000)
	PixelBot.statistics.add("type1.item2", 1);
	PixelBot.sleep(3000)
	PixelBot.statistics.add("type1", 1);
	PixelBot.sleep(3000)
	PixelBot.statistics.add("type1.item1", 1);
	PixelBot.sleep(3000)
	PixelBot.statistics.add("type1.item2", 1);
	PixelBot.sleep(3000)
	PixelBot.statistics.add("type1.item3", 1);
	PixelBot.sleep(3000)
	PixelBot.statistics.add("type2.item1", 1);
	PixelBot.sleep(3000)
	PixelBot.statistics.add("type2.item1", 1);
	PixelBot.sleep(3000)

	return

	

			

	

	PixelBot.browser.detect();
	PixelBot.state.update();
	// Travel.to_church('люди');
	// Travel.to_church('подводный мир');
	return

	

			

	

	PixelBot.log.info(params['fight']['heal']['switch'] ? '2' : '1');
	return

	

			

	

	PixelBot.browser.detect();

	while (true) {
		if (PixelBot.grab_find_rect('интерфейс.Бой.Пояс.1', 0, 355, 55, 25)) {
			PixelBot.find_click_rect('интерфейс.Бой.Пояс.право', 2, 0, 355, 55,
					25)
			PixelBot.log.info('Всего 1');
		} else if (PixelBot
				.grab_find_rect('интерфейс.Бой.Пояс.2', 0, 355, 55, 25)) {
			PixelBot
					.find_click_rect('интерфейс.Бой.Пояс.лево', 2, 0, 355, 55,
							25)
			PixelBot.log.info('Всего 2');
		}

		PixelBot.sleep(2000);
	}

	return;

	// var d = PixelBot.browser.get();
	for ( var i = 0; i < 5; ++i) {
		// PixelBot.grab();
		// PixelBot.find_click_rect('интерфейс.Верхняя панель.Локация',1, 0,
		// 0, d.width, 50)

		PixelBot.mouse.move(50, 20 * i);

		PixelBot.log.info('Всего: ');
		PixelBot.sleep(3000);
	}
	return

	

			

	

		

	

	for ( var i = 0; i < 5; ++i) {
		PixelBot.sleep(3000);
		PixelBot.log.info('Всего: ');
		PixelBot.key.click(0x42, 0);
	}
	return;

	Travel.to_church('люди');
	return;
	PixelBot.sound.play('notify.wav');

	PixelBot.grab();
	HHTools.solveCaptcha('texture', 0, 0, 1000, 500);

	return;
	PixelBot.browser.detect();

	HHTools.fight();

	return;
	HHTools.pickupTool({
		'resource' : {
			'type' : 'texture'
		}
	})
	// HHTools.pickupTool({'resource':{'type':'relic'}})
	return;
	PixelBot.log.info('aa ' + HHTools.init());
	HHTools.scroll(function() {
		PixelBot.sleep(1000);
	});
	HHTools.scroll(function() {
		PixelBot.sleep(1000);
	});

	// HHTools.scroll_to('r', 0);
	// PixelBot.sleep(3000)
	// HHTools.scroll_to('l', 100);
	return;

	PixelBot.grab_find_click("локации.люди.перелёт.День добрый", 1)

	// Tools.sayText ('aaaa' , 'клановый');

	// PixelBot.sound.play('notify.wav');
	// var a = Script.loadExcel("config/pixelbot.xlsx");
	// Script.saveConfiguration(a, "config/pixelbot.cfg");

	// var superhit_file = Helper.loadConfiguration('t.hits');
	// if (prev_sequence != null) {
	// superhit_file[prev_sequence] = superhit_file[prev_sequence] + 1;
	// Script.saveConfiguration(superhit_file,
	// params['superhit']['superhit_file'])
	// }

	/*
	 * var sizes = []; var size = -1; var total = 0; for ( var n in
	 * superhit_file) { ++total; if (size < 0 || size > superhit_file[n]) {
	 * prev_sequence = n; size = superhit_file[n]; } while (sizes.length <=
	 * superhit_file[n]) { sizes.push(0); } sizes[superhit_file[n]] =
	 * sizes[superhit_file[n]] + 1; } hit_sequence = prev_sequence.split('');
	 * hit_sequence = hit_sequence.reverse();
	 * 
	 * PixelBot.log.info('Всего: ' + total + ', Проверено: ' + (total -
	 * sizes[size]) + ', Осталось: ' + sizes[size] + ', Проверяем: [' +
	 * hit_sequence + '], Итерация: ' + size + '=[' + sizes + '] ');
	 */

	// HHTools.pickupTool ({'resource':{'type':'edge'}})
	// var s = "sss";
}

Test.configuration = {
	"key1" : 'value',
	"key2" : {
		"sub_key1" : 'value1',
		"dub_key2" : 'value2',
	}
}