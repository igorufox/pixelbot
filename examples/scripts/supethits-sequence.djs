SuperhitsSeq = new function() {
};

SuperhitsSeq.prototype = {};

SuperhitsSeq.params = {
	'active' : true,
	'get_sequence' : function(count) {
		var result = [];
		for ( var i = 1; i <= count; ++i) {
			result.push({
				name : 'hit_' + i,
				type : 'radio',
				values : [ 'Голова', 'Торс', 'Ноги' ]
			});
		}
		return result;
	},
	'contains' : function(array, value) {
		if (array == undefined)
			return -1;
		for ( var i = 0; i < array.length; ++i) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	},
	'config' : function(params) {
		return [
				{
					name : 'level',
					label : 'Укажите Ваш уровень',
					type : 'list',
					values : [ '1', '2', '3', '4', '5', '6', '7', '8', '9',
							'10' ]
				},
				{
					name : 'search_hit',
					label : 'Подбираем супер-удар',
					type : 'list',
					values : [ 'Сила Волка', 'Великая Сила Волка',
							'Жажда Крови', 'Ярость Пантеры', 'Атака Змеи',
							'Великая Жажда Крови', 'Ярость Тигра',
							'Смертельная Атака Змеи', 'Удар Медведя',
							'Гнев Дракона' ].slice(0, params.level)
				},
				{
					name : 'know_hits',
					label : 'Известные супер-удары',
					type : params.level >= 2 ? 'checkgroup' : 'hidden',
					values : function(thiz) {
						var array = [ 'Сила Волка', 'Великая Сила Волка',
								'Жажда Крови', 'Ярость Пантеры', 'Атака Змеи',
								'Великая Жажда Крови', 'Ярость Тигра',
								'Смертельная Атака Змеи', 'Удар Медведя',
								'Гнев Дракона' ].slice(0, params.level);
						var index = thiz.contains(array, params.search_hit);
						return [].concat(array.slice(0, index), array
								.slice(index + 1));
					}(this)
				},
				{
					name : 'superhit_1',
					label : 'Сила Волка',
					type : this.contains(params.know_hits, 'Сила Волка') != -1 ? 'sequence'
							: 'hidden',
					values : this.get_sequence(2)
				},
				{
					name : 'superhit_2',
					label : 'Великая Сила Волка',
					type : this
							.contains(params.know_hits, 'Великая Сила Волка') != -1 ? 'sequence'
							: 'hidden',
					values : this.get_sequence(3)
				},
				{
					name : 'superhit_3',
					label : 'Жажда Крови',
					type : this.contains(params.know_hits, 'Жажда Крови') != -1 ? 'sequence'
							: 'hidden',
					values : this.get_sequence(4)
				},
				{
					name : 'superhit_4',
					label : 'Ярость Пантеры',
					type : this.contains(params.know_hits, 'Ярость Пантеры') != -1 ? 'sequence'
							: 'hidden',
					values : this.get_sequence(4)
				},
				{
					name : 'superhit_5',
					label : 'Атака Змеи',
					type : this.contains(params.know_hits, 'Атака Змеи') != -1 ? 'sequence'
							: 'hidden',
					values : this.get_sequence(5)
				},
				{
					name : 'superhit_6',
					label : 'Великая Жажда Крови',
					type : this.contains(params.know_hits,
							'Великая Жажда Крови') != -1 ? 'sequence'
							: 'hidden',
					values : this.get_sequence(5)
				},
				{
					name : 'superhit_7',
					label : 'Ярость Тигра',
					type : this.contains(params.know_hits, 'Ярость Тигра') != -1 ? 'sequence'
							: 'hidden',
					values : this.get_sequence(6)
				},
				{
					name : 'superhit_8',
					label : 'Смертельная Атака Змеи',
					type : this.contains(params.know_hits,
							'Смертельная Атака Змеи') != -1 ? 'sequence'
							: 'hidden',
					values : this.get_sequence(6)
				},
				{
					name : 'superhit_9',
					label : 'Удар Медведя',
					type : this.contains(params.know_hits, 'Удар Медведя') != -1 ? 'sequence'
							: 'hidden',
					values : this.get_sequence(7)
				},
				{
					name : 'superhit_10',
					label : 'Гнев Дракона',
					type : this.contains(params.know_hits, 'Гнев Дракона') != -1 ? 'sequence'
							: 'hidden',
					values : this.get_sequence(7)
				} ];
	},
	'defaults' : function() {
		return {
			'limit' : '12',
			'timer' : '1500'
		}
	},
	'name' : 'Генерация возможных суперударов'
}
SuperhitsSeq.main = function(params) {
	var lens = [ 2, 3, 4, 4, 5, 5, 6, 6, 7, 7 ];

	var count = lens[[ 'Сила Волка', 'Великая Сила Волка', 'Жажда Крови',
			'Ярость Пантеры', 'Атака Змеи', 'Великая Жажда Крови',
			'Ярость Тигра', 'Смертельная Атака Змеи', 'Удар Медведя',
			'Гнев Дракона' ].indexOf(params['search_hit'])];

	var all = this.generate_all_possible(count);
	var known = this.get_known_hits(count, params);
	PixelBot.log.info('known: ' + known.length + ' [' + known + ']');
	var pre_result = this.exclude_excess_hits(all, known);
	PixelBot.log.info('pre_result: ' + pre_result.length + ' [' + pre_result
			+ ']');

	var result = {};
	for ( var i = 0; i < pre_result.length; ++i) {
		result["_" + pre_result[i]] = 0;
	}

	Helper.saveAsConfiguration(result, [ {
		name : 'Cписок возможных суперударов',
		extension : 'hits'
	} ]);
}

SuperhitsSeq.generate_all_possible = function(count) {
	PixelBot.log.info("генерируем список возможных суперударов из " + count
			+ " ударов.");
	if (count == 0) {
		return [ '' ];
	}
	var result = [];
	var prev = this.generate_all_possible(count - 1);
	for ( var i = 0; i < prev.length; ++i) {
		result.push(prev[i] + '0');
		result.push(prev[i] + '1');
		result.push(prev[i] + '2');
	}
	return result;
}
SuperhitsSeq.get_known_hits = function(count, params) {
	var result = [];
	for ( var i = 1; i <= count; ++i) {
		var r = '';
		var j = 1;
		var hit = params['superhit_' + i];
		while (hit['hit_' + j]) {
			switch (hit['hit_' + j]) {
			case 'Голова':
				r += '0';
				break;
			case 'Торс':
				r += '1';
				break;
			case 'Ноги':
				r += '2';
				break;
			}
			++j;
		}
		if (r != '') {
			result.push(r);
		}

	}
	return result;
}

SuperhitsSeq.exclude_excess_hits = function(all, known) {
	PixelBot.log.info("удаляем лишние суперудары");
	var result = [];
	for ( var i = 0; i < all.length; ++i) {
		var contains = false;

		for ( var j = 0; j < known.length; ++j) {
			if (all[i].indexOf(known[j]) != -1) {
				contains = true;
				break;
			}
		}

		if (!contains) {
			result.push(all[i]);
		}
	}
	return result;

}
