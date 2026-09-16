Travel = new function() {};

Travel.prototype = {};

Travel.params = {
	'active' : true,
	'config' : function(params) {
		return [{
			name : 'type',
			label : 'Регион',
			type : 'radio',
			values : PixelBot.get_elements("локации", 1)
		}, {
			name : 'target',
			label : 'Цель',
			type : 'list',
			values : PixelBot.get_elements("локации." + params['type'] + ".тексты")
		}, {
			name : 'defend',
			label : 'Защищатся при нападении',
			type : 'check'
		}];
	},
	'defaults' : {
		type : 'люди',
		target : 'Площадь О\'Дельвайса',
		defend : false
	},
	name : 'Путешествовать'
}

Travel.main = function(params) {
	PixelBot.browser.detect();
	this.to(params['type'], params['target'], params['defend'], null, params);
}

Travel.to_church = function(type, mode, defend, params) {
	if (typeof type == 'undefined') {
		type = 'люди';
	}
	if (typeof mode == 'undefined') {
		mode = 0;
	}

	var cur_location = this.detect_curent_location(type, defend, params);
	if (cur_location == null) {
		PixelBot.log.error("Не возможно определить текущую локацию");
		return;
	}
	var base_location = cur_location;
	if (mode == 1) {
		if (PixelBot.grab_find('интерфейс.Сообщения.Желаете воскреснуть')) {
			if (!PixelBot.find_click('интерфейс.Кнопки.Да', 1)) {
				PixelBot.find_click('интерфейс.Кнопки.В столице', 1)
			}
			PixelBot.sleep(10000);
			PixelBot.grab_find_click("интерфейс.Кнопки.Закрыть", 1);
		}
	} else {
		this.to(type, 'Храм', 0, function(graph) {
			if (graph['Переправа грифона']) {
				delete graph['Переправа грифона'].transitions['Луанское побережье'];
			}
			if (graph['Переправа Виверна']) {
				delete graph['Переправа Виверна'].transitions['Вотчина скал'];
			}
		}, params);
	}

	PixelBot.log.success("Воскресли идем назад");
	this.to(type, base_location, null, params);

}

Travel.to = function(type, to, defend, modifier, params) {
	var graph = this.get_graph(type, modifier);
	do {
		var cur_location = this.detect_curent_location(type, defend, params);
		if (cur_location == null) {
			PixelBot.log.error("Не возможно определить текущую локацию");
			return false;
		}
		PixelBot.log.info('Текущая локация: ' + cur_location + ', Целевая локация: ' + to);
		var path = Travel.dijkstra.find_path(graph, cur_location, to);
		PixelBot.log.info('Найденный путь:' + path);

		var time = Travel.calculate_time(graph, path);
		PixelBot.log.info('Ориентировочное время в пути ' + Math.floor(time / 60) + 'м ' + time % 60 + 'c');
	} while (!this.travel_path(type, path, defend, graph, params));

	PixelBot.log.success('Прибыли в место назначения');
	return true;
}

Travel.calculate_time = function(graph, path) {
	var time = 0;
	for (var i = 1; i < path.length; ++i) {
		time += graph[path[i - 1]].transitions[path[i]].c;
	}
	return time;
}

Travel.enterInLocation = function(defend, params) {
	var d = PixelBot.browser.get();
	if (PixelBot.grab_find_click_rect('интерфейс.Верхняя панель.Локация', 1, 0, 0, d.width, 50)) {
		for (var i = 0; i < 5; ++i) {
			PixelBot.sleep(1000);
			PixelBot.grab();
			if (defend) {
				Fight.check(params.fight);
			}
			if (PixelBot.find('интерфейс.Локации')) {
				PixelBot.log.success('Зашли в локацию');
				return true;
			}
			if (PixelBot.find('интерфейс.Поместье.В Локацию')) {
				PixelBot.log.success('Зашли в локацию');
				return true;
			}
			if (PixelBot.grab_find_rect("интерфейс.Бой.Vs", 0, 80, d.width, 50)){
				if (defend) {
					Fight.check(params.fight);
				}
			}
		}
	}
	PixelBot.log.warning('Не вижу кнопки входа в локация, поводим мышкой - может появится');
	if (PixelBot.grab_find_click("интерфейс.Кнопки.Закрыть", 1)) {
		PixelBot.sleep(1000);
		PixelBot.grab();
	}
	PixelBot.mouse.move(0, 25);
	PixelBot.mouse.move(d.width, 25);
	PixelBot.mouse.move(0, 25);
	if (PixelBot.grab_find_click_rect('интерфейс.Верхняя панель.Локация', 1, 0, 0, d.width, 50)) {
		for (var i = 0; i < 15; ++i) {
			PixelBot.sleep(1000);
			PixelBot.grab();
			if (defend) {
				Fight.check(params.fight);
			}
			if (PixelBot.find('интерфейс.Локации')) {
				PixelBot.log.success('Зашли в локацию');
				return true;
			}
			if (PixelBot.find('интерфейс.Поместье.В Локацию')) {
				PixelBot.log.success('Зашли в локацию');
				return true;
			}
			if (PixelBot.grab_find_rect("интерфейс.Бой.Vs", 0, 80, d.width, 50)){
				if (defend) {
					Fight.check(params.fight);
				}
			}
		}

	}
	PixelBot.log.error('Не вижу кнопки входа в локацию');
	return false;
}

Travel.check = function(defend, params) {
	if (defend) {
		Fight.check(params.fight);
	}
	if (!PixelBot.grab_find('интерфейс.Локации') && !PixelBot.grab_find('интерфейс.Поместье.В Локацию')) {
		this.enterInLocation(defend, params);
	}
}

Travel.detect_curent_location = function(type, defend, params) {
	var d = PixelBot.browser.get();
	this.check(defend, params);
	var loc_names = PixelBot.get_elements("локации." + type + ".тексты");
	var graph = this.get_graph(type);
	for (var i = 0; i < loc_names.length; ++i) {

		if (PixelBot.find_rect("локации." + type + ".тексты." + loc_names[i], d.width / 2 - 200, 80, 350, 30)) {
			if (graph['' + loc_names[i]]) {
				PixelBot.link.setLocation('' + loc_names[i]);
				return '' + loc_names[i];
			} else {
				if (PixelBot.grab_find('интерфейс.Локации') || PixelBot.find('интерфейс.Поместье.В Локацию')) {
					var loc_ = PixelBot.last_find_rect;
					for ( var loc in graph) {
						if (graph[loc].label == '' + loc_names[i]) {
							var r = true;
							for ( var t in graph[loc].transitions) {
								var transition = graph[loc].transitions[t];
								if (!PixelBot.grab_find_rect("локации." + type + ".переходы." + transition.t, loc_.x - 60, loc_.y, 200, 250)) {
									r = false;
									break;
								}
							}
							if (r) {
								PixelBot.link.setLocation(loc);
								return loc;
							}
						}
					}
				}
				return null;
			}
		}
	}
	return null;
}

Travel.travel_path = function(type, path, defend, graph, params) {
	for (var i = 0; i < path.length - 1; ++i) {
		var location = path[i];
		var cur_location = this.detect_curent_location(type, defend, params);

		// PixelBot.log.error(location.getClass());
		// PixelBot.log.error(cur_location.getClass());
		if (!location.equals(cur_location))
			return false;

		var loc_desc = graph[location];
		var transition = loc_desc.transitions[path[i + 1]];
		var target = path[i + 1];
		if (graph[target].label)
			target = graph[target].label;
		if (!this.click_on_transition(type, transition, target, defend, params)) {
			return false;
		};
		PixelBot.sleep(1000);
	}

	// var cur_location = this.detect_curent_location(type);
	// return path[path.length - 1].equals(cur_location);
	return true;
}

Travel.graph = null;
Travel.get_graph = function(type, modifier) {
	// Travel.graph = null;
	if (Travel.graph == null || Travel.graph == undefined || true) {
		// Read travel data
		var a = Helper.loadConfiguration("config/travel.cfg");

		// convert to internal format
		var r = {};
		for ( var b in a) {
			r[b] = {}
			for ( var c in a[b]) {
				r[b][c] = {}
				if (a[b][c].l) {
					r[b][c].label = a[b][c].l
				}
				r[b][c].transitions = {};
				for ( var d in a[b][c].t) {
					r[b][c].transitions[d] = {
						'c' : a[b][c].c,
						't' : a[b][c].t[d]
					}
					if (a[b][d] && a[b][d].action)
						r[b][c].transitions[d].action = a[b][d].action;
				}
			}
		}
		Travel.graph = r;
	}

	var graph = Travel.graph.human;
	if (type == 'люди') {
		graph = Travel.graph.human;
	} else if (type == 'магмары') {
		graph = Travel.graph.magmars;
	} else if (type == 'арена') {
		graph = Travel.graph.arena;
	} else if (type == 'пещеры') {
		graph = Travel.graph.caves;
	} else if (type == 'подводный мир') {
		graph = Travel.graph.underwater;
	} else if (type == 'туманные острова') {
		graph = Travel.graph['misty island'];
	}

	if (modifier)
		modifier(graph);

	return graph;
}

Travel.click_on_transition = function(type, transition, target, defend, params) {
	PixelBot.grab();
	if (PixelBot.find('интерфейс.Локации') || PixelBot.find('интерфейс.Поместье.В Локацию')) {
		var loc = PixelBot.last_find_rect;
		if (typeof transition.t == 'string' || transition.t.wait) {
			if (PixelBot.grab_square(0xA100000, 0xA10000, loc.x - 10, loc.y + 260, 75, 10)) {
				PixelBot.log.info('ждем время перехода');
				while (PixelBot.grab_square(0xB00000, 0xFF0000, loc.x - 60, loc.y + 240, 170, 15)) {
					PixelBot.sleep(1000);
					if (defend) {
						Fight.check(params.fight);
					}
				}
				PixelBot.sleep(1500);
			}
		}
		PixelBot.log.info('кликнем на переход ' + transition.t);
		var d = PixelBot.browser.get();
		var c = 0;
		while (true) {
			this.check(defend, params);
			if (c++ > 10) {
				this.enterInLocation(defend, params);
				return false;
			}

			if (typeof transition.t == 'string') {
				PixelBot.grab_find_click_rect("локации." + type + ".переходы." + transition.t, 1, loc.x - 60, loc.y, 200, 250);
			} else if (transition.t.action) {
				if (transition.t.action(loc))
					break;
			}

			PixelBot.sleep(2000);
			if (PixelBot.grab_find('интерфейс.Сообщения.Перегруз')) {
				if (PixelBot.find_click("интерфейс.Кнопки.Закрыть", 1)) {
					break;
				}
			}

			if (transition.action) {
				if (transition.action(loc))
					break;
			}

			if (PixelBot.grab_find_rect("локации." + type + ".тексты." + target, d.width / 2 - 200, 80, 350, 30)) {
				break;
			}

		}
	}
	return true;
}

Travel.dijkstra = {
	single_source_shortest_paths : function(graph, s, d) {
		// Predecessor map for each node that has been encountered.
		// node ID => predecessor node ID
		var predecessors = {};

		// Costs of shortest paths from s to all nodes encountered. node ID =>
		// cost
		var costs = {};
		costs[s] = 0;

		// Costs of shortest paths from s to all nodes encountered; differs from
		// `costs` in that it provides easy access to the node that currently
		// has the known shortest path from s.
		// XXX: Do we actually need both `costs` and `open`?
		var open = this.PriorityQueue.make();
		open.push(s, 0);

		var closest, u, cost_of_s_to_u, adjacent_nodes, cost_of_e, cost_of_s_to_u_plus_cost_of_e, cost_of_s_to_v, first_visit;
		while (open) {
			// In the nodes remaining in graph that have a known cost from s,
			// find the node, u, that currently has the shortest path from s.
			closest = open.pop();
			if (closest == undefined) {
				throw "Couldn't find path from " + s + " to " + d;
			}
			u = closest.value;
			cost_of_s_to_u = closest.cost;

			// Get nodes adjacent to u...
			adjacent_nodes = graph[u] ? graph[u].transitions || {} : {};

			// ...and explore the edges that connect u to those nodes, updating
			// the cost of the shortest paths to any or all of those nodes as
			// necessary. v is the node across the current edge from u.
			for ( var v in adjacent_nodes) {
				// Get the cost of the edge running from u to v.
				cost_of_e = adjacent_nodes[v].c;

				// Cost of s to u plus the cost of u to v across e--this is *a*
				// cost from s to v that may or may not be less than the current
				// known cost to v.
				cost_of_s_to_u_plus_cost_of_e = cost_of_s_to_u + cost_of_e;

				// If we haven't visited v yet OR if the current known cost from
				// s to
				// v is greater than the new cost we just found (cost of s to u
				// plus
				// cost of u to v across e), update v's cost in the cost list
				// and
				// update v's predecessor in the predecessor list (it's now u).
				cost_of_s_to_v = costs[v];
				first_visit = (typeof costs[v] === 'undefined');
				if (first_visit || cost_of_s_to_v > cost_of_s_to_u_plus_cost_of_e) {
					costs[v] = cost_of_s_to_u_plus_cost_of_e;
					open.push(v, cost_of_s_to_u_plus_cost_of_e);
					predecessors[v] = u;
				}

				// If a destination node was specified and we reached it, we're
				// done.
				if (v === d) {
					open = null;
					break;
				}
			}
		}

		if (typeof costs[d] === 'undefined') {
			var msg = ['Could not find a path from ', s, ' to ', d, '.'].join('');
			throw new Error(msg);
		}

		return predecessors;
	},

	extract_shortest_path_from_predecessor_list : function(predecessors, d) {
		var nodes = [];
		var u = d;
		var predecessor;
		while (u) {
			nodes.push(u);
			predecessor = predecessors[u];
			u = predecessors[u];
		}
		nodes.reverse();
		return nodes;
	},

	find_path : function(graph, s, d) {
		var predecessors = this.single_source_shortest_paths(graph, s, d);
		var r = this.extract_shortest_path_from_predecessor_list(predecessors, d);
		return r;
	},

	/**
	 * A very naive priority queue implementation.
	 */
	PriorityQueue : {
		make : function(opts) {
			var T = this, t = {}, opts = opts || {}, key;
			for (key in T) {
				t[key] = T[key];
			}
			t.queue = [];
			t.sorter = opts.sorter || T.default_sorter;
			return t;
		},

		default_sorter : function(a, b) {
			return a.cost - b.cost;
		},

		/**
		 * Add a new item to the queue and ensure the highest priority element
		 * is at the front of the queue.
		 */
		push : function(value, cost) {
			var item = {
				value : value,
				cost : cost
			};
			this.queue.push(item);
			this.queue.sort(this.sorter);
		},

		/**
		 * Return the highest priority element in the queue.
		 */
		pop : function() {
			return this.queue.shift();
		}
	}
}
Travel.externals = {
	'flight' : function(loc, transition) {
		// "локации.магмары.переходы.Погонщик Везур"
		// "локации.люди.переходы.Погонщик Луран"
		if (PixelBot.grab_find('интерфейс.Сообщения.Желаете воскреснуть')) {
			if (!PixelBot.find_click('интерфейс.Кнопки.Да', 1)) {
				PixelBot.find_click('интерфейс.Кнопки.В столице', 1)
			}
			PixelBot.sleep(10000);
			PixelBot.grab_find_click("интерфейс.Кнопки.Закрыть", 1);
			PixelBot.sleep(2000);
			return false;
		}

		PixelBot.grab_find_click_rect(transition, 1, loc.x - 60, loc.y, 200, 250);
		PixelBot.sleep(2000);

		if (PixelBot.grab_find_click("локации.перелёт.Перелет через замок", 1)) {
			PixelBot.sleep(2000);
		}
		if (PixelBot.grab_find_click("локации.перелёт.День добрый", 1)) {
			PixelBot.sleep(2000);
		}
		if (PixelBot.grab_find_click("локации.перелёт.Нет проблем", 1)) {
			PixelBot.sleep(2000);
		}
		if (PixelBot.grab_find_click("интерфейс.Кнопки.Далее", 1)) {
			PixelBot.sleep(2000);
			return true;
		}

		if (PixelBot.grab_find("интерфейс.Сообщения.Вы являетесь призраком")) {
			PixelBot.sleep(2000);
			if (PixelBot.grab_find_click("интерфейс.Кнопки.Закрыть", 1)) {
				Travel.enterInLocation();
				PixelBot.sleep(2000);
				if (PixelBot.grab_find('интерфейс.Сообщения.Желаете воскреснуть')) {
					if (!PixelBot.find_click('интерфейс.Кнопки.Да', 1)) {
						PixelBot.find_click('интерфейс.Кнопки.В столице', 1)
					}
					PixelBot.sleep(10000);
					PixelBot.grab_find_click("интерфейс.Кнопки.Закрыть", 1);
				}
			}
		}
		return false;
	},
	'temple' : function(loc) {
		if (PixelBot.grab_find("интерфейс.Сообщения.Вы воскрешены") || PixelBot.find("интерфейс.Сообщения.Воскрешать можно только призраков")) {
			PixelBot.find_click("интерфейс.Кнопки.Закрыть", 1);
		} else if (PixelBot.grab_find("интерфейс.Сообщения.Вы не можете воскрешаться, так как ваша связь души с телом временно нарушена")
				|| PixelBot.grab_find("интерфейс.Сообщения.Магическая энергия этого обелиска не успела восстановиться, зайдите попозже")) {
			PixelBot.find_click("интерфейс.Кнопки.Закрыть", 1);
			PixelBot.log.info('Обелиск не доступен, ждем 3 мин');
			PixelBot.sleep(20000);
		}
		PixelBot.state.update();
		return PixelBot.state.health.cur > 0;
	},
	'shop' : function(loc) {
		return PixelBot.grab_find("интерфейс.Магазин.Магазин(А)");
	}
}