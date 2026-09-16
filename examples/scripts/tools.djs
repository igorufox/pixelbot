Tools = new function() {};

Tools.prototype = {};

Tools.enterIn = function(target, check_elem) {
	var d = PixelBot.browser.get();
	if (PixelBot.grab_find_click_rect('интерфейс.Верхняя панель.' + target, 1, 0, 0, d.width, 60)) {
		for ( var i = 0; i < 5; ++i) {
			PixelBot.sleep(3000);
			PixelBot.grab();
			if (PixelBot.find(check_elem)) {
				PixelBot.log.success('Зашли в ' + target);
				return true;
			} else if (PixelBot.find_rect("интерфейс.Бой.Vs", 0, 80, d.width, 50)) {
				PixelBot.log.info('напали');
				PixelBot.find_click_rect('интерфейс.Бой.выход.выход', 1, PixelBot.last_find_rect.x - 100, PixelBot.last_find_rect.y + 50, 250, 250);
				return false;
			}
		}
	}
	PixelBot.log.warning('Не вижу кнопки входа в ' + target + ', поводим мышкой - может появится');
	if (PixelBot.grab_find_click_rect('интерфейс.Кнопки.Закрыть', 1, 0, 0, d.width, d.height)) {
		PixelBot.grab(0, 0, d.width, d.height);
	}

	PixelBot.mouse.move(0, 25);
	PixelBot.mouse.move(d.width, 25);
	PixelBot.mouse.move(0, 25);
	if (PixelBot.grab_find_click_rect('интерфейс.Верхняя панель.' + target, 1, 0, 0, d.width, 50)) {
		for ( var i = 0; i < 15; ++i) {
			PixelBot.sleep(3000);
			PixelBot.grab();
			if (PixelBot.find(check_elem)) {
				PixelBot.log.success('Зашли в ' + target);
				return true;
			} else if (PixelBot.find_rect("интерфейс.Бой.Vs", 0, 80, d.width, 50)) {
				PixelBot.find_click_rect('интерфейс.Бой.выход.выход', 1, PixelBot.last_find_rect.x - 100, PixelBot.last_find_rect.y + 50, 250, 250);
				PixelBot.log.info('напали');
				return false;
			}
		}
	}
	PixelBot.log.error('Не вижу кнопки входа в ' + target);
	return false;
}

Tools.selectBackpack = function(target) {
	var d = PixelBot.browser.get();

	while (true) {
		Tools.enterIn("Рюкзак", "интерфейс.Рюкзак.Вместимость");
		var i = 0;
		PixelBot.log.info('Заходим в интерфейс.Рюкзак.' + target);
		while (!PixelBot.grab_find("интерфейс.Рюкзак." + target + "(А)")) {
			PixelBot.grab_find_click("интерфейс.Рюкзак." + target + "(П)", 1);
			PixelBot.sleep(5000);
			if (i++ == 5) {
				PixelBot.log.warning('Не вижу: интерфейс.Рюкзак.' + target + "(П)");
				break;
			}
		}
		if (PixelBot.grab_find("интерфейс.Рюкзак." + target + "(А)")) {
			break;
		}
		PixelBot.sleep(3000);
	}

}

Tools.sayText = function(text, target) {
	var d = PixelBot.browser.get();

	if (target) {
		if (PixelBot.grab_find('интерфейс.Чат.правая часть')) {
			var r = PixelBot.last_find_rect;
			var c = 0;
			while (!PixelBot.grab_find_rect('интерфейс.Чат.каналы.А.' + target, 30, r.y, r.x - 400, 40)) {
				PixelBot.find_click_rect('интерфейс.Чат.каналы.П.' + target, 1, 30, r.y, r.x - 400, 40);
				PixelBot.sleep(3000);
				if (++c > 5) {
					return;
				}
			}
		}
	}
	PixelBot.move_click(150, d.height - 15, 1);

	PixelBot.key.paste(text);
	PixelBot.key.click(PixelBot.key.VK_ENTER, 0);

	return true;
}
