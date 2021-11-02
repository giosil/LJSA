namespace GUI {

    export class AppTableActions extends WUX.WComponent {
        left: WUX.WContainer;
        right: WUX.WContainer;

        constructor(id: string) {
            // WComponent init
            super(id, 'AppTableActions', null, 'table-actions-wrapper');
            // AppTableActions init
            this.left = new WUX.WContainer(this.subId('l'), 'left-actions');
            this.right = new WUX.WContainer(this.subId('r'), 'right-actions');
        }

        protected componentDidMount(): void {
            let $i = $('<div class="table-actions clearfix" data-b2x-sticky-element="1" data-b2x-sticky-element-z-index="3"></div>');
            this.root.append($i);
            this.left.mount($i);
            this.right.mount($i);
        }

        setLeftVisible(v: boolean) {
            this.left.visible = v;
        }

        setRightVisible(v: boolean) {
            this.right.visible = v;
        }
    }

    export class LJSASelServizi extends WUX.WSelect2 {
        constructor(id?: string, multiple?: boolean) {
            super(id);
            this.multiple = multiple;
            this.name = 'CFSelServizi';
        }

        protected componentDidMount(): void {
            let user = GUI.getUserLogged();
            jrpc.execute('SERVIZI.lookup', [user.groups], (result) => {
                let data = [];
                for (var i = 0; i < result.length; i++) {
                    var r = result[i];
                    var d = { id: r[0], text: r[2] };
                    data.push(d);
                }
                let options: Select2Options = {
                    data: data,
                    placeholder: "",
                    allowClear: true,
                };
                this.init(options);
            });
        }
    }
}