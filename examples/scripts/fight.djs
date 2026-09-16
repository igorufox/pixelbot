Fight = new function() {
};

Fight.prototype = {};

Fight.params = {
	'get_sequence' : function(count) {
		var result = [];
		for (var i = 1; i <= count; ++i) {
			result.push({
				name : 'hit_' + i,
				type : 'radio',
				values : [ 'Голова', 'Торс', 'Ноги' ]
			});
		}
		return result;
	},
	'config' : function(params) {
		if (!params) {
			params = {};
		}
		if (params['heal'] && typeof params['heal']['heal_scrolls'] == 'string') {
			params['heal']['heal_scrolls'] = eval(params['heal']['heal_scrolls'])
		}
		if (params['heal'] && typeof params['heal']['heal_elixirs'] == 'string') {
			params['heal']['heal_elixirs'] = eval(params['heal']['heal_elixirs'])
		}

		return [
				{
					name : 'general',
					label : 'Общие',
					type : 'group',
					values : [ {
						name : 'use_mana',
						label : 'Использовать магию',
						type : 'check',
					}, {
						name : 'fight_in_group',
						label : 'Вступать в бой в группе',
						type : 'check',
						disabled : true
					} ]
				},
				{
					name : 'heal',
					label : 'Параметры лечения',
					type : 'group',
					values : [
							{
								name : 'switch',
								label : 'Переключать пояс',
								type : 'check',
							},
							{
								name : 'gigant',
								label : 'Используем гиг в начале боя',
								type : 'check',
							},
							{
								name : 'gigant_type',
								label : 'Тип эликсира гиганта',
								type : params['heal']
										&& params['heal']['gigant'] ? 'checkgroup'
										: 'hidden',
								values : PixelBot
										.get_elements("интерфейс.Бой.Эликсир гиганта"),
							},
							{
								name : 'heal',
								label : 'Лечиться в бою',
								type : 'check'
							},
							{
								name : 'heal_scrolls',
								label : 'Тип свитка исцеления',
								type : params['heal'] && params['heal']['heal'] ? 'checkgroup'
										: 'hidden',
								values : PixelBot
										.get_elements("интерфейс.Бой.Свитки исцеления")

							},
							{
								name : 'heal_ratio_s',
								label : 'Используем свиток исцеления если жизни осталось меньше (%)',
								type : params['heal']
										&& params['heal']['heal']
										&& params['heal']['heal_scrolls']
										&& params['heal']['heal_scrolls'].length > 0 ? 'spinner'
										: 'hidden'
							},
							{
								name : 'heal_elixirs',
								label : 'Тип эликсира жизни',
								type : params['heal'] && params['heal']['heal'] ? 'checkgroup'
										: 'hidden',
								values : PixelBot
										.get_elements("интерфейс.Бой.Эликсир жизни")

							},
							{
								name : 'heal_ratio_e',
								label : 'Используем эликсир жизни если жизни осталось меньше (%)',
								type : params['heal']
										&& params['heal']['heal']
										&& params['heal']['heal_elixirs']
										&& params['heal']['heal_elixirs'].length > 0 ? 'spinner'
										: 'hidden'
							},
							{
								name : 'heal_delay',
								label : 'Если полечились тормозим (сек)',
								type : params['heal']
										&& params['heal']['heal']
										&& ((params['heal']['heal_scrolls'] && params['heal']['heal_scrolls'].length > 0) || (params['heal']['heal_elixirs'] && params['heal']['heal_elixirs'].length > 0)) ? 'spinner'
										: 'hidden',
								values : [ 0, 15, 1 ],
							},
							{
								name : 'mana_gigant',
								label : 'Используем магическое усиление в начале боя',
								type : params['general']
										&& params['general']['use_mana'] ? 'check'
										: 'hidden'
							},
							{
								name : 'mana_gigant_type',
								label : 'Тип магического усиления',
								type : params['general']
										&& params['general']['use_mana']
										&& params['heal']
										&& params['heal']['mana_gigant'] === true ? 'checkgroup'
										: 'hidden',
								values : PixelBot
										.get_elements("интерфейс.Бой.Усиление магия"),
							},

							{
								name : 'mana',
								label : 'Востaнавливать ману в бою',
								type : params['general']
										&& params['general']['use_mana'] ? 'check'
										: 'hidden'
							},
							{
								name : 'mana_elixirs',
								label : 'Тип эликсира маны',
								type : params['general']
										&& params['general']['use_mana']
										&& params['heal']
										&& params['heal']['mana'] === true ? 'checkgroup'
										: 'hidden',
								values : PixelBot
										.get_elements("интерфейс.Бой.Эликсир маны")
							},
							{
								name : 'mana_ratio',
								label : 'Используем эликсир маны если маны осталось меньше (%)',
								type : params['general']
										&& params['general']['use_mana']
										&& params['heal']
										&& params['heal']['mana']
										&& params['heal']['mana_elixirs']
										&& params['heal']['mana_elixirs'].length > 0 ? 'spinner'
										: 'hidden'
							},
							{
								name : 'mana_delay',
								label : 'Если выпили тормозим (сек)',
								type : params['general']
										&& params['general']['use_mana']
										&& params['heal']
										&& params['heal']['mana']
										&& params['heal']['mana_elixirs']
										&& params['heal']['mana_elixirs'].length > 0 ? 'spinner'
										: 'hidden',
								values : [ 0, 15, 1 ]
							} ]
				},
				{
					name : 'hitpower',
					label : 'Усиление удара',
					type : 'group',
					values : [
							{
								name : 'use',
								label : 'Пьем эликсир на удар',
								type : 'radio',
								values : [ 'Не пьем', 'На каждый удар',
										'На супер удар' ]
							},
							{
								name : 'type',
								label : 'Тип усиления удара',
								type : params['hitpower']
										&& ((params['hitpower']['use'] == 'На каждый удар') || (params['hitpower']['use'] == 'На супер удар')) ? 'checkgroup'
										: 'hidden',
								values : PixelBot
										.get_elements("интерфейс.Бой.Усиление удара")
							} ]
				},
				{
					name : 'superhit',
					label : 'Супер-удар',
					type : 'group',
					values : [
							{
								name : 'block',
								label : 'Вставать в блок',
								type : 'radio',
								values : [ 'бой без блока',
										'блок с выходом на суперудар',
										'постоянный блок' ]
							},
							{
								name : 'sleep_time',
								label : 'Пауза перед ударом',
								type : 'spinner',
								values : [ 0, 15, 1 ]
							},
							{
								name : 'superhit_mode',
								label : 'Супер-удар',
								type : 'radio',
								values : [ 'Случайно', 'Подбирать', 'Известен' ]
							},
							{
								name : 'superhit_file',
								label : 'Файл сгенерированый скриптом "Генерация возможных суперударов"',
								type : params['superhit']
										&& params['superhit']['superhit_mode'] == 'Подбирать' ? 'file'
										: 'hidden',
								values : [ {
									name : 'Cписок возможных суперударов',
									extension : 'hits'
								} ]
							},
							{
								name : 'superhit',
								label : 'Известный супер-удар',
								type : params['superhit']
										&& params['superhit']['superhit_mode'] == 'Известен' ? 'list'
										: 'hidden',
								values : [ 'Сила Волка', 'Великая Сила Волка',
										'Жажда Крови', 'Ярость Пантеры',
										'Атака Змеи', 'Великая Жажда Крови',
										'Ярость Тигра',
										'Смертельная Атака Змеи',
										'Удар Медведя', 'Гнев Дракона' ]
							},
							{
								name : 'superhit_sequence',
								label : params['superhit'] ? params['superhit']['superhit']
										: "",
								type : params['superhit']
										&& params['superhit']['superhit_mode'] == 'Известен'
										&& params['superhit']['superhit'] ? 'sequence'
										: 'hidden',
								values : this.get_sequence(function(params) {
									if (!params['superhit'])
										return 1;
									switch (params['superhit']['superhit']) {
									case 'Сила Волка':
										return 2;
									case 'Великая Сила Волка':
										return 3;
									case 'Жажда Крови':
										return 4;
									case 'Ярость Пантеры':
										return 4;
									case 'Атака Змеи':
										return 5;
									case 'Великая Жажда Крови':
										return 5;
									case 'Ярость Тигра':
										return 6;
									case 'Смертельная Атака Змеи':
										return 6;
									case 'Удар Медведя':
										return 7;
									case 'Гнев Дракона':
										return 7;
									default:
										return 1;
									}

								}(params))
							} ]
				},
				{
					name : 'wraith',
					label : 'Мороки',
					type : 'group',
					values : [
							{
								name : 'use_mount',
								label : 'Использовать ездовое животное',
								type : 'check'
							},
							{
								name : 'use_wraith',
								label : 'Используем морок',
								type : 'check',
								disabled : true
							},
							{
								name : 'wraith_type',
								label : 'Тип морока',
								type : params['wraith']
										&& params['wraith']['use_wraith'] ? 'checkgroup'
										: 'hidden',
								values : PixelBot
										.get_elements("интерфейс.Бой.Амулеты"),
								disabled : true
							} ]
				},
				{
					name : 'after',
					label : 'После боя',
					type : 'group',
					values : [
							{
								name : 'health',
								label : 'Востонавливать здоровье',
								type : 'check'
							},
							{
								name : 'eat',
								label : 'Закусывать',
								type : params['after']
										&& params['after']['health'] ? 'check'
										: 'hidden'
							},
							{
								name : 'eat_procent',
								label : 'Закусывать если жизни меньше %',
								type : params['after']
										&& params['after']['health']
										&& params['after']['eat'] ? 'spinner'
										: 'hidden',
								values : [ 0, 100, 1 ]
							},
							{
								name : 'eat_type',
								label : 'Что кушать',
								type : params['after']
										&& params['after']['health']
										&& params['after']['eat'] ? 'radio'
										: 'hidden',
								values : [ 'Еда', 'Рыба' ]
							},
							{
								name : 'food',
								label : params['after']
										&& params['after']['eat_type'] ? params['after']['eat_type']
										: '',
								type : params['after']
										&& params['after']['health']
										&& params['after']['eat'] ? 'selector'
										: 'hidden',
								values : function() {
									if (params['after']
											&& params['after']['health']) {
										if (params['after']['eat_type'] == 'Еда') {
											return PixelBot
													.get_elements("еда.Эффекты")
										} else if (params['after']['eat_type'] == 'Рыба') {
											return PixelBot
													.get_elements("еда.Разное")
										}
									}
									return [];
								}()

							},
							{
								name : 'mana',
								label : 'Востонавливать ману',
								type : params['general']
										&& params['general']['use_mana'] ? 'check'
										: 'hidden'
							},
							{
								name : 'imman',
								label : 'Выпивать',
								type : params['general']
										&& params['general']['use_mana']
										&& params['after']
										&& params['after']['mana'] ? 'check'
										: 'hidden'
							},
							{
								name : 'imman_procent',
								label : 'Выпивать если маны меньше %',
								type : params['general']
										&& params['general']['use_mana']
										&& params['after']
										&& params['after']['mana']
										&& params['after']['imman'] ? 'spinner'
										: 'hidden',
								values : [ 0, 100, 1 ]
							},
							{
								name : 'imman_type',
								label : 'Алкоголь',
								type : params['general']
										&& params['general']['use_mana']
										&& params['after']
										&& params['after']['mana']
										&& params['after']['imman'] ? 'selector'
										: 'hidden',
								values : PixelBot.get_elements("еда.Имман")
							},

					]
				} ];
	},
	'defaults' : function() {
		return {
			heal : true
		}
	}
}

//
// parameter(пьем вампирик на суперудар, false, check_box)
// parameter(используем морок против группы, false, check_box)
//
// parameter(тип свитка исцеления, серый, radio_button, зеленый, синий)
// parameter(тип эликсира жизни, серый, radio_button, зеленый, синий, великий,
// подземный)
// parameter(тип эликсира гиганта, серый, radio_button, зеленый, синий, великий,
// подземный)
// parameter(тип амулета, муха, radio_button, пхад, тигр, игуарон, голем)
// parameter(если полечились тормозим (сек), 5, text_field)
//
//
//
// parameter(вступать в бой в группе, false, check_box)
// parameter(используем хилки во время боя, false, check_box)
// parameter(используем гиги во время боя, false, check_box)
// parameter(используем гиг в начале боя, false, check_box)
// parameter(перекусывать после боя, false, check_box)
// parameter(перед боем дойти до указанной локации, false, check_box)
// parameter(используем свиток исцеления если жизни осталось меньше (%),50,
// text_field)
// parameter(используем эликсир жизни если жизни осталось меньше (%),20,
// text_field)
// parameter(время работы бота(мин), 240, text_field)

Fight.vs_location = null;

Fight.main = function(params) {
	PixelBot.browser.detect();
	// this.init();
	this.check(params);
	// var d = PixelBot.browser.get();
	// if (PixelBot.grab_find_rect("интерфейс.Бой.Vs", 0, 70, d.width, 30)) {
	// this.vs_location = PixelBot.last_find_rect;
	// this.process();
	// }
}

Fight.init = function(params) {
	PixelBot.state.update();
}

Fight.check = function(params) {
	var d = PixelBot.browser.get();
	if (PixelBot.grab_find_rect("интерфейс.Бой.Vs", 0, 80, d.width, 50)) {
		this.vs_location = PixelBot.last_find_rect;
		this.process(params);
		return true;
	}
	return false;
}

Fight.process = function(params) {
	var d = PixelBot.browser.get();
	var hit_sequence = [];
	var prev_sequence = null;

	for (var t = 0; t < 10; ++t) {
		if (PixelBot.grab_find_rect("интерфейс.Бой.Меч", this.vs_location.x - 25,
				this.vs_location.y + 180, 75, 75)) {
			break;
		}
		if (PixelBot.find_rect("интерфейс.Бой.Магия", this.vs_location.x - 25,
				this.vs_location.y + 180, 75, 75)) {
			if (!params.general.use_mana) {
				PixelBot.log.info('Переключаемся на физу');
				PixelBot.move_elem();
				PixelBot.click(1);
			}
			break;
		}
		PixelBot.sleep(1000);
	}
	PixelBot.log.info('Начало боя');

	if (params['heal']['gigant']
			|| (params.general.use_mana && params['heal']['mana_gigant'])) {

		var belts = [ {
			'current' : function() {
				return 1;
			},
			'ratio' : 2,
			'elixirs' : function() {
				var result = [];
				for (var i = 0; i < params['heal']['gigant_type'].length; ++i) {
					result.push('Эликсир гиганта.'
							+ params['heal']['gigant_type'][i]);
				}
				return result;
			}(),
			'delay' : 0,
			'break' : true
		} ];
		if (params.general.use_mana) {
			belts
					.push({
						'current' : function() {
							return 1;
						},
						'ratio' : 2,
						'elixirs' : function() {
							var result = [];
							for (var i = 0; i < params['heal']['mana_gigant_type'].length; ++i) {
								result
										.push('Усиление магия.'
												+ params['heal']['mana_gigant_type'][i]);
							}
							return result;
						}(),
						'delay' : 0,
						'break' : false
					});
		}

		Fight.belt(belts, params);
	}

	if (params['wraith']['use_mount']) {
		PixelBot.log.info('Призовем ездовое животное');
		PixelBot.grab_find_click_rect('интерфейс.Правая панель.Животное', 1,
				d.width - 50, 0, 50, d.height);
		PixelBot.sleep(3000);
		PixelBot.grab_find_click('интерфейс.Кнопки.Выполнить', 1);
	}

	var belts = [];
	if (params['heal']['heal'] && params['heal']['heal_elixirs']
			&& params['heal']['heal_elixirs'].length != 0) {
		belts
				.push({
					'current' : PixelBot.state.health.ratio,
					'ratio' : params['heal']['heal_ratio_e'],
					'elixirs' : function() {
						var result = [];
						for (var i = 0; i < params['heal']['heal_elixirs'].length; ++i) {
							result.push('Эликсир жизни.'
									+ params['heal']['heal_elixirs'][i]);
						}
						return result;
					}(),
					'delay' : isNaN(params['heal']['heal_delay']) ? 0
							: params['heal']['heal_delay'],
					'break' : true
				});

	}
	if (params['heal']['heal'] && params['heal']['heal_scrolls']
			&& params['heal']['heal_scrolls'].length != 0) {
		belts
				.push({
					'current' : PixelBot.state.health.ratio,
					'ratio' : params['heal']['heal_ratio_s'],
					'elixirs' : function() {
						var result = [];
						for (var i = 0; i < params['heal']['heal_scrolls'].length; ++i) {
							result.push('Свитки исцеления.'
									+ params['heal']['heal_scrolls'][i]);
						}
						return result;
					}(),
					'delay' : isNaN(params['heal']['heal_delay']) ? 0
							: params['heal']['heal_delay'],
					'break' : true
				});

	}
	if (params.general.use_mana && params['heal']['mana']
			&& params['heal']['mana_elixirs']
			&& params['heal']['mana_elixirs'].length != 0) {
		belts
				.push({
					'current' : PixelBot.state.mana.ratio,
					'ratio' : params['heal']['mana_ratio'],
					'elixirs' : function() {
						var result = [];
						for (var i = 0; i < params['heal']['mana_elixirs'].length; ++i) {
							result.push('Эликсир маны.'
									+ params['heal']['mana_elixirs'][i]);
						}
						return result;
					}(),
					'delay' : isNaN(params['heal']['mana_delay']) ? 0
							: params['heal']['mana_delay'],
					'break' : true
				});

	}

	var treshold = 0;
	while (true) {
		PixelBot.grab_rect(this.vs_location.x - 100, this.vs_location.y + 50,
				250, 250);
		if (PixelBot.find_rect('интерфейс.Бой.Победа', this.vs_location.x - 100,
				this.vs_location.y + 50, 250, 250)) {
			PixelBot.statistics.add("Бой", +1);
			break;
		} else if (PixelBot.find_rect('интерфейс.Бой.Поражение',
				this.vs_location.x - 100, this.vs_location.y + 50, 250, 250)) {
			PixelBot.statistics.add("Бой", -1);
			PixelBot.log.info('убили');
			break;
		} else if (PixelBot.find_click_rect('интерфейс.Бой.выход.выход', 1,
				this.vs_location.x - 100, this.vs_location.y + 50, 250, 250)) {
			PixelBot.statistics.add("Бой", -1);
			PixelBot.log.info('Какая-то ошибка');
			PixelBot.sleep(5000);
			break;
		} else if (PixelBot.find_click_rect('интерфейс.Бой.Обновить', 1,
				this.vs_location.x - 100, this.vs_location.y + 50, 250, 250)) {
			PixelBot.log.warning('Соединение прервано');
		}

		Fight.belt(belts, params);

		if (PixelBot.find_rect("интерфейс.Бой.Меч", this.vs_location.x - 25,
				this.vs_location.y + 180, 75, 75)) {
			treshold = 0;

			if (hit_sequence.length == 0) {
				if (params['superhit']['superhit_mode'] == 'Известен') {
					var hits = params['superhit']['superhit_sequence'];
					hit_sequence = [];
					for (var i = 1; hits['hit_' + i]; ++i) {
						hit_sequence.push(hits['hit_' + i]);
					}
					hit_sequence = hit_sequence.reverse();
				} else if (params['superhit']['superhit_mode'] == 'Подбирать') {
					var superhit_file = Helper
							.loadConfiguration(params['superhit']['superhit_file']);
					if (!superhit_file)
						superhit_file = {};
					if (prev_sequence != null) {
						superhit_file[prev_sequence] = (superhit_file[prev_sequence] ? superhit_file[prev_sequence]
								: 0) + 1;
						Helper.saveConfiguration(superhit_file,
								params['superhit']['superhit_file'])
					}

					var sizes = [];
					var size = -1;
					var total = 0;
					for ( var n in superhit_file) {
						++total;
						if (size < 0 || size > superhit_file[n]) {
							prev_sequence = '' + n;
							size = superhit_file[n];
						}
						while (sizes.length <= superhit_file[n]) {
							sizes.push(0);
						}
						sizes[superhit_file[n]] = sizes[superhit_file[n]] + 1;

					}
					if (!prev_sequence) {
						PixelBot.log
								.warning('Не разобрал файл с супер ударами, использую случайный');
						prev_sequence = '' + Math.floor(Math.random() * 3);
					}
					hit_sequence = prev_sequence.split('');
					PixelBot.log.success('Всего: ' + total + ', Проверено: '
							+ (total - sizes[size]) + ', Осталось: '
							+ sizes[size] + ', Проверяем: [' + hit_sequence
							+ '], Итерация: ' + size + '=[' + sizes + '] ');

					hit_sequence = hit_sequence.reverse();
					hit_sequence.pop();
				} else {
					hit_sequence = [ Math.floor(Math.random() * 3) ];
				}
			}

			PixelBot.sleep(params['superhit']['sleep_time'] * 1000);

			var sword_location = PixelBot.last_find_rect;
			if (params['superhit']['block'] == 'бой без блока') {
				PixelBot.find_click_rect("интерфейс.Бой.Блок.активный", 1,
						sword_location.x - 60, sword_location.y - 30, 30, 70);
			} else if (params['superhit']['block'] == 'постоянный блок') {
				PixelBot.find_click_rect("интерфейс.Бой.Блок.пассивный", 1,
						sword_location.x - 60, sword_location.y - 30, 30, 70);
			} else if (params['superhit']['block'] == 'блок с выходом на суперудар') {
				if (hit_sequence.length != 1) {
					PixelBot.find_click_rect("интерфейс.Бой.Блок.пассивный", 1,
							sword_location.x - 60, sword_location.y - 30, 30,
							70);
				} else {
					PixelBot.find_click_rect("интерфейс.Бой.Блок.активный", 1,
							sword_location.x - 60, sword_location.y - 30, 30,
							70);
				}
			}

			if (params['hitpower']
					&& ((params['hitpower']['use'] == 'На каждый удар') || ((params['hitpower']['use'] == 'На супер удар') && (hit_sequence.length == 1)))) {
				Fight
						.belt(
								[ {
									'current' : function() {
										return 1;
									},
									'ratio' : 2,
									'elixirs' : function() {
										var result = [];
										for (var i = 0; i < params['hitpower']['type'].length; ++i) {
											result
													.push('Усиление удара.'
															+ params['hitpower']['type'][i]);
										}
										return result;
									}(),
									'delay' : 0,
									'break' : true
								} ], params);
			}

			PixelBot.last_find_rect = sword_location;
			var hit = hit_sequence.pop();
			switch (hit) {
			case 0:
			case '0':
			case 'Голова':
				PixelBot.log.trace('Удар по голове');
				PixelBot.move_rel_click(50, -30, 1);
				break;
			case 1:
			case '1':
			case 'Торс':
				PixelBot.log.trace('Удар по торсу');
				PixelBot.move_rel_click(70, 20, 1);
				break;
			case 2:
			case '2':
			case 'Ноги':
				PixelBot.log.trace('Удар по ногам');
				PixelBot.move_rel_click(50, 70, 1);
				break;
			}
		} else if (PixelBot.find_rect("интерфейс.Бой.Магия",
				this.vs_location.x - 25, this.vs_location.y + 180, 75, 75)) {
			treshold = 0;
			var sword_location = PixelBot.last_find_rect;
			if (!params.general.use_mana) {
				PixelBot.log.info('Переключаемся на физу 2');
				PixelBot.move_elem();
				PixelBot.click(1);
			} else {
				PixelBot.last_find_rect = sword_location;
				PixelBot.move_rel_click(15, -40, 1);
			}

		} else {
			if (treshold++ > 100)
				break;
		}

		PixelBot.sleep(1000);
	}
	PixelBot.move_rel_click(15, 65, 1);

	PixelBot.state.update();
	var alive = PixelBot.state.health.cur > 0;
	PixelBot.log.info('alive' + alive);
	if (alive) {
		PixelBot.log.info('alive2' + alive);
		Fight.after(params);
	}

	PixelBot.sleep(1000);
	// var ii = 0;
	// while (!PixelBot.grab_find_click_rect('интерфейс.Бой.выход.В охоту', 1, 0,
	// 100, d.width, 100)) {
	// PixelBot.sleep(1000);
	// if (ii++ > 30)
	// break;
	// }
	// PixelBot.sleep(3000);
	return alive;
}

Fight.belt = function(belts, params) {
	var d = PixelBot.browser.get();
	PixelBot.state.update();
	for (var b = 0; b < belts.length; ++b) {
		var belt = belts[b];
		// PixelBot.log.info('current ' + belt['current']() + 'эликсир' +
		// belt['ratio'] + ' result' + (belt['current']() < belt['ratio']));
		if (belt['current']() < belt['ratio']) {
			var used = false;

			for (var b_s = 0; b_s < (params['heal']['switch'] ? 2 : 1); ++b_s) {
				for (var i = 0; i < belt['elixirs'].length; ++i) {
					PixelBot.log.info('Используем ' + belt['elixirs'][i]
							+ 'эликсир');
					if (PixelBot.grab_find_click_rect('интерфейс.Бой.'
							+ belt['elixirs'][i], 1, 0, 0, 50, d.height)) {
						PixelBot.move_rel(40, 0);
						if (belt['break']) {
							used = true;
							break;
						}
					}
				}
				if (used || !params['heal']['switch']) {
					break;
				}

				if (PixelBot.grab_find_rect('интерфейс.Бой.Пояс.1', 0, 355, 55,
						25)) {
					PixelBot.find_click_rect('интерфейс.Бой.Пояс.право', 2, 0,
							355, 55, 25)
				} else if (PixelBot.grab_find_rect('интерфейс.Бой.Пояс.2', 0,
						355, 55, 25)) {
					PixelBot.find_click_rect('интерфейс.Бой.Пояс.лево', 2, 0,
							355, 55, 25)
				}
			}

			if (used) {
				PixelBot.log.info('Ждем ' + belt['delay'] + ' секунд');
				PixelBot.sleep(belt['delay'] * 1000)
				if (PixelBot
						.grab_find("интерфейс.Сообщения.Вы не можете использовать эффекты вне боя")) {
					if (PixelBot.find_click("интерфейс.Кнопки.Закрыть", 1)) {
						PixelBot.sleep(1000);
						PixelBot.grab();
					}
				}
			}
		}
	}
}

Fight.after = function(params) {
	if (params['after']['health']) {
		var k = 0;
		var cur_health = 0
		var prev_health = 0;
		do {
			PixelBot.state.update();
			if (params['after']['eat']) {
				PixelBot.log.info(''
						+ params['after']['eat_procent']
						+ ' < '
						+ PixelBot.state.health.ratio()
						+ ' = '
						+ (params['after']['eat_procent'] < PixelBot.state.health
								.ratio()));
				if (params['after']['eat_procent'] > PixelBot.state.health
						.ratio()) {
					var backpack = null;
					if (params['after']['eat_type'] == 'Еда') {
						backpack = 'Эффекты'
					} else if (params['after']['eat_type'] == 'Рыба') {
						backpack = 'Разное'
					}
					if (backpack != null) {
						Tools.selectBackpack(backpack);
						var food = params['after']['food'];
						for (var fi = 0; fi < food.length; ++fi) {
							PixelBot.log.info('Кушаем ' + food[fi]);
							if (PixelBot.find_click('еда.' + backpack + "."
									+ food[fi], 1)) {
								for (var bi = 0; bi < 30; bi++) {
									PixelBot.sleep(1000);
									if (PixelBot.grab_find_click(
											'интерфейс.Кнопки.Выполнить', 1)) {
										break;
									}
								}
								do {
									PixelBot.sleep(1000);
								} while (PixelBot.grab_find_click(
										'интерфейс.Кнопки.Выполнить', 1));
								break;
							}
						}
					}
				}

			}
			PixelBot.sleep(2000);
			cur_health = PixelBot.state.health.cur;
			if (cur_health > prev_health) {
				k = 0;
				prev_health = cur_health;
			}
			if (++k > 30)
				break;

		} while (PixelBot.state.health.max - 5 > cur_health);
	}

	if (params.general.use_mana && params['after']['mana']) {
		var k = 0;
		var cur_mana = 0
		var prev_mana = 0;
		do {
			PixelBot.state.update();
			if (params['after']['imman']) {
				PixelBot.log.info(''
						+ params['after']['imman_procent']
						+ ' < '
						+ PixelBot.state.mana.ratio()
						+ ' = '
						+ (params['after']['imman_procent'] < PixelBot.state.mana
								.ratio()));
				if (params['after']['imman_procent'] > PixelBot.state.mana
						.ratio()) {
					Tools.selectBackpack("Эффекты");
					var food = params['after']['imman_type'];
					for (var fi = 0; fi < food.length; ++fi) {
						PixelBot.log.info('Пьем ' + food[fi]);
						if (PixelBot.find_click('еда.Имман.' + food[fi], 1)) {
							do {
								PixelBot.sleep(1000);
							} while (PixelBot.grab_find_click(
									'интерфейс.Кнопки.Выполнить', 1));
							break;
						}
					}
				}

			}
			PixelBot.sleep(2000);
			cur_mana = PixelBot.state.mana.cur;
			if (cur_mana > prev_mana) {
				k = 0;
				prev_mana = cur_mana;
			}
			if (++k > 30)
				break;

		} while (PixelBot.state.mana.max - 5 > cur_mana);
	}
}
