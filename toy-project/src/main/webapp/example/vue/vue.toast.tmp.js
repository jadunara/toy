
var list = [ 
	    {
	        name: 'Kiss and Make Up',
	        artist: 'Dua Lipa',
	        score: '5'
	      },
	      {
	        name: 'Bohemian Rhapsody',
	        artist: 'Queen',
	        score: '2'
	      },
	      {
	        name: 'Done For Me',
	        artist: 'Charlie Puth',
	        score: '3'
	      },
	      {
	        name: 'thank u, next',
	        artist: 'Ariana Grande',
	        score: '4'
	      },
	      {
	        name: 'Handclap',
	        artist: 'Fitz & The Tantrums',
	        score: '1'
	      }	
      ] ;
	 var props = {
		data: {
		  type: [Array, Object],
		  required: true
		},
		columns: {
		  type: Array,
		  required: true
		},
		options: {
		  type: Object,
		  default() {
			return {};
		  }
		},
		// @deprecated. You should use it via importing tui-grid directly.
		theme: {
		  type: [String, Object],
		  validator(value) {
			let result = false;
			if (typeof value === 'string') {
			  result = presetTheme.indexOf(value) > -1;
			} else {
			  result = value.hasOwnProperty('name') && value.hasOwnProperty('value');
			}
			return result;
		  }
		},
		// @deprecated. You should use it via importing tui-grid directly.
		language: {
		  type: [String, Object],
		  validator(value) {
			let result = false;
			if (typeof value === 'string') {
			  result = presetLanguage.indexOf(value) > -1;
			} else {
			  result = value.hasOwnProperty('name') && value.hasOwnProperty('value');
			}
			return result;
		  }
		}
	  };
	  var lvMyTheme = {
		name: 'myTheme',
		value: {
		  cell: {
			normal: {
			  background: '#00ff00',
			  border: '#e0e0e0',
			},
			header: {
			  background: '#ff0000',
			  border: '#ffff00',
			},
			editable: {
			  background: '#fbfbfb',
			},
		  },
		},
	  };
var lvGridData  = [
	{
		name: 'Kiss and Make Up',
		artist: 'Dua Lipa',
		score: '5',
	  },
	  {
		name: 'Bohemian Rhapsody',
		artist: 'Queen',
		score: '2',
	  },
	  {
		name: 'Done For Me',
		artist: 'Charlie Puth',
		score: '3',
	  },
	  {
		name: 'thank u, next',
		artist: 'Ariana Grande',
		score: '4',
	  },
	  {
		name: 'Handclap',
		artist: 'Fitz & The Tantrums',
		score: '1',
	  },
	  {
		name: 'Shape Of You',
		artist: 'Ed Sheeran',
		score: '5',
	  },
	  {
		name: 'Snowman',
		artist: 'Sia',
		score: '5',
	  },
	  {
		name: "Don't Stop Me Now ",
		artist: 'Queen',
		score: '3',
	  },
	  {
		name: 'Havana',
		artist: 'Camila Cabello',
		score: '2',
	  },
	  {
		name: 'A No No',
		artist: 'Mariah Carey',
		score: '5',
	  }	
]
;
 var Grid = new tui.Grid();

var vm = new Vue({
		  el : '#app'
		, components: {
			grid: Grid,
		}
		, template : '#demo_template2'
		, data : function() { return {
			 selected : 3
			, gridProps : null
			}
		}
		, mount : function() {

		}
		// , props : props
		, created() {

		    // this.gridProps = {
		    //     data:  list
		    //   , el : document.getElementById("tuiGrid")
		    //   ,pageOptions: {
		    //     perPage: 1
		    //   }
		    //   , columns: [
		    //     { name: "userId", header: "아이디", align: "center" },
		    //     { name: "userNm", header: "이름", align: "center" },
		    //     { name: "regDt", header: "가입일", align: "center" }
		    //   ]
			//   , pageOptions: { perPage: 1 }
			// 		, columns: [
			// 			{ name: "userId", header: "아이디", align: "center" },
			// 			{ name: "userNm", header: "이름", align: "center" },
			// 			{ name: "regDt", header: "가입일", align: "center" }
			// 		]
			// 		, options: { rowHeaders: ['checkbox'] }
			// 		, myTheme : {
			//         name : 'myTheme',
			//         value: {
			//             cell: {
			//               normal: { background: '#00ff00', border: '#e0e0e0' },
			//               header: { background: '#ff0000', border: '#ffff00' },
			//               editable: { background: '#fbfbfb' }
			//             }
			//           }
			//         }			  
		    // }
			this.gridProps = {
				rowHeaders: ['checkbox', 'rowNum'],
				columnOptions: {
				  resizable: true,
				  frozenCount: 1,
				},
				columns: [
				  {
					header: 'Name',
					name: 'name',
					editor: 'text',
				  },
				  {
					header: 'Artist',
					name: 'artist',
				  },
				  {
					header: 'Personal Score',
					name: 'score',
					onBeforeChange(ev) {
					  console.log('executes before the value changes : ', ev);
					},
					onAfterChange(ev) {
					  console.log('executes after the value has changed : ', ev);
					},
					copyOptions: {
					  useListItemText: true,
					},
					formatter: 'listItemText',
					editor: {
					  type: 'radio',
					  options: {
						listItems: [
						  {
							text: '★☆☆☆☆',
							value: '1',
						  },
						  {
							text: '★★☆☆☆',
							value: '2',
						  },
						  {
							text: '★★★☆☆',
							value: '3',
						  },
						  {
							text: '★★★★☆',
							value: '4',
						  },
						  {
							text: '★★★★★',
							value: '5',
						  },
						],
					  },
					},
				  },
				],
				data: lvGridData,
				myTheme: lvMyTheme
				, options: {
				  rowHeaders: ['checkbox'],
				},
			  };
			}
		,methods: {
			  onCheck(ev) {
				console.log('check event: ', ev);
			  },
			  onUnCheck(ev) {
				console.log('uncheck event: ', ev);
			  },
			}
		, updated : function() {
			console.log('vm #el updated');
		}

		});
		//vm.mount('#app');