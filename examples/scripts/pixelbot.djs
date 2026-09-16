PixelBot = new function() {
};

PixelBot.prototype = {};

PixelBot.last_find_rect = null;

PixelBot.sleep = function(millis) {
	Packages.java.lang.Thread.sleep(millis);
}

PixelBot.log = {
	fatal : function(text) {
		return Helper.log("Fatal", text);
	},
	error : function(text) {
		return Helper.log("Error", text);
	},
	warning : function(text) {
		return Helper.log("Warning", text);
	},
	info : function(text) {
		return Helper.log("Info", text);
	},
	trace : function(text) {
		return Helper.log("Trace", text);
	},
	success : function(text) {
		return Helper.log("Success", text);
	}
}

PixelBot.browser = {
	detect : function() {
		return Helper.browser.detect();
	},
	get : function() {
		return Helper.browser.get();
	}
}

PixelBot.grab = function() {
	return Helper.grabScreen();
}

PixelBot.grab_rect = function(x, y, width, height) {
	return Helper.grabScreen(Helper.getRectangle(x, y, width, height));
}

PixelBot.get_elements = function(category, level) {
	if (level) {
		return Helper.getElementsDesc(category, level);
	} else {
		return Helper.getElementsDesc(category);
	}
}

PixelBot.highlight_region = function(x, y, width, height) {
	return Helper.highlightRegion('scheme4', Helper.getRectangle(x, y, width,
			height));
}

PixelBot.find_click = function(elem_name, count) {
	if (this.find_move(elem_name)) {
		return this.click(count);
	}
	return false;
}

PixelBot.find_click_rect = function(elem_name, count, x, y, width, height) {
	if (this.find_move_rect(elem_name, x, y, width, height)) {
		return this.click(count);
	}
	return false;
}

PixelBot.find_move = function(elem_name) {
	if (this.find(elem_name)) {
		return this.move_elem();
	}
	return false;
}

PixelBot.find_move_rect = function(elem_name, x, y, width, height) {
	if (this.find_rect(elem_name, x, y, width, height)) {
		return this.move_elem();
	}
	return false;
}

PixelBot.find = function(elem_name) {
	this.last_find_rect = Helper.findElement(elem_name);
	if (this.last_find_rect != null) {
		return true;
	}
	return false;
}

PixelBot.find_rect = function(elem_name, x, y, width, height) {
	this.last_find_rect = Helper.findElement(elem_name, Helper.getRectangle(x,
			y, width, height));
	// this.log.trace(elem_name + ' ' + this.last_find_rect);
	if (this.last_find_rect != null) {
		return true;
	}
	return false;
}

PixelBot.find_many = function(elem_name) {
	return Helper.findElements(elem_name);
}

PixelBot.find_many_rect = function(elem_name, x, y, width, height) {
	return Helper.findElements(elem_name, Helper.getRectangle(x, y, width,
			height));
}

PixelBot.grab_find = function(elem_name) {
	this.grab();
	return this.find(elem_name);
}

PixelBot.grab_find_click = function(elem_name, count) {
	this.grab();
	return this.find_click(elem_name, count);
}

PixelBot.grab_find_click_rect = function(elem_name, count, x, y, width, height) {
	this.grab_rect(x, y, width, height);
	return this.find_click_rect(elem_name, count, x, y, width, height);
}

PixelBot.grab_find_rect = function(elem_name, x, y, width, height) {
	this.grab_rect(x, y, width, height);
	return this.find_rect(elem_name, x, y, width, height);
}

PixelBot.move = function(x, y) {
	return Helper.mouseMove(x, y);
}

PixelBot.move_rel = function(dx, dy) {
	if (this.last_find_rect == null) {
		this.last_find_rect = {
			x : 0,
			y : 0
		}
	}
	return this.move(this.last_find_rect.x + dx, this.last_find_rect.y + dy);
}

PixelBot.move_rel_click = function(dx, dy, N) {
	if (this.move_rel(dx, dy)) {
		return this.click(N);
	}
	return false;
}

PixelBot.move_click = function(x, y, N) {
	if (this.move(x, y)) {
		return this.click(N);
	}
	return false;
}

PixelBot.move_elem = function() {
	var x = this.last_find_rect.x + this.last_find_rect.width * Math.random();
	var y = this.last_find_rect.y + this.last_find_rect.height * Math.random()
	return this.move(x, y);
}

PixelBot.click = function(N) {
	Helper.mouseClick(N);
	return true;
}

PixelBot.mouse = {
	click : function(N) {
		return Helper.mouseClick(N);
	},
	press : function(button) {
		return Helper.mousePress(button);
	},
	release : function(button) {
		return Helper.mouseRelease(button);
	},
	move : function(x, y) {
		return Helper.mouseMove(x, y);
	}
}

PixelBot.key = {
	click : function(key, modifier) {
		return Helper.keyClick(key, modifier);
	},
	press : function(key) {
		return Helper.keyPress(key);
	},
	release : function(key) {
		return Helper.keyRelease(key);
	},
	print : function(text) {
		return Helper.printText(text);
	},
	paste : function(text) {
		return Helper.pasteText(text);
	},
	SHIFT_MASK : 1 << 0,
	CTRL_MASK : 1 << 1,
	ALT_MASK : 1 << 3,
	VK_A : 0x41,
	VK_B : 0x42,
	VK_C : 0x43,
	VK_D : 0x44,
	VK_E : 0x45,
	VK_F : 0x46,
	VK_G : 0x47,
	VK_H : 0x48,
	VK_I : 0x49,
	VK_J : 0x4A,
	VK_K : 0x4B,
	VK_L : 0x4C,
	VK_M : 0x4D,
	VK_N : 0x4E,
	VK_O : 0x4F,
	VK_P : 0x50,
	VK_Q : 0x51,
	VK_R : 0x52,
	VK_S : 0x53,
	VK_T : 0x54,
	VK_U : 0x55,
	VK_V : 0x56,
	VK_W : 0x57,
	VK_X : 0x58,
	VK_Y : 0x59,
	VK_Z : 0x5A,
	VK_ENTER : 0x0A,
	VK_BACK_SPACE : 0x08
}

PixelBot.square = function(color_min, color_max, x, y, width, height) {
	return Helper.square(color_min, color_max, Helper.getRectangle(x, y, width,
			height));
}

PixelBot.grab_square = function(color_min, color_max, x, y, width, height) {
	this.grab_rect(x, y, width, height);
	return this.square(color_min, color_max, x, y, width, height)
}

PixelBot.state = {
	update : function() {
		PixelBot.grab_rect(90, 35, 155, 15);
		var state = Helper.recognizeText(0xb0, 0xff, Helper.getRectangle(90,
				35, 155, 15));
		try {
			var space = state.indexOf(' ');
			if (space != -1) {
				this.mana.max = parseInt(state
						.substring(state.lastIndexOf('%') + 1));
				this.mana.cur = parseInt(state.substring(space, state
						.lastIndexOf('%')));
				state = state.substring(0, space);
			} else {
				this.mana.max = 0;
				this.mana.cur = 0;
			}

			this.health.max = parseInt(state
					.substring(state.lastIndexOf('%') + 1));
			this.health.cur = parseInt(state.substring(0, state
					.lastIndexOf('%')));
			Helper.statistics('state', [ this.health.cur, this.health.max,
					this.mana.cur, this.mana.max ], 0);
		} catch (e) {
		}
		// PixelBot.log.trace(this.health.cur + '/' + this.health.max + ' '
		// + this.mana.cur + '/' + this.mana.max + ' ');
	},
	health : {
		max : 0,
		cur : 0,
		ratio : function() {
			return Math.round(PixelBot.state.health.cur * 100
					/ PixelBot.state.health.max);
		}
	},
	mana : {
		max : 0,
		cur : 0,
		ratio : function() {
			return Math.round(PixelBot.state.mana.cur * 100
					/ PixelBot.state.mana.max);
		}
	}
}

PixelBot.statistics = {
	add : function(data, value) {
		Helper.statistics('statistics', data, value);
	}
}

PixelBot.screen = {
	get_pixel : function(x, y) {
		return Helper.getPixel(x, y);
	},
	get_raw : function() {
		return Helper.getPixels();
	}
}

PixelBot.sound = {
	play : function(file) {
		return Helper.playSound(file);
	}
}

PixelBot.link = {
	setLocation : function(location) {
		return Helper.link.setLocation(location);
	},
	splinter : {
		request : function(server, race, location, nick) {
			return Helper.link.splinterRequest(server, race, location, nick);
		},
		response : function(server, race, location) {
			return Helper.link.splinterResponse(server, race, location);
		},
		update : function(server, nick) {
			return Helper.link.splinterUpdate(server, nick);
		}

	},
	grabElement : function(name, x, y, width, height) {
		var elem = Helper.grabElement(x, y, width, height);
		Helper.link.postElement(name, elem);
	}
}

PixelBot.solveCaptcha = function(type, x, y, width, height, param) {
	this.grab_rect(x, y, width, height);
	var elem = Helper.grabElement(x, y, width, height);
	return Helper.solveCaptcha(type, elem, param);
}

