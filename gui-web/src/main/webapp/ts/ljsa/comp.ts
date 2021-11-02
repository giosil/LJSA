namespace GUI {

    import WUtil = WUX.WUtil;

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

        protected updateState(nextState: any): void {
            super.updateState(nextState);
            // Servizio di default
            let s = WUtil.toString(nextState);
            if(!s) _defService = s;
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

    export class LJSASelClassi extends WUX.WSelect2 {

        constructor(id?: string, multiple?: boolean) {
            super(id, [], multiple);
            this.name = 'LJSASelClassi';
        }

        protected componentDidMount(): void {
            let options: Select2Options = {
                ajax: {
                    dataType: "json",
                    delay: 400,
                    processResults: function (result, params) {
                        return {
                            results: result
                        };
                    },
                    transport: function (params: JQueryAjaxSettings, success?: (data: any) => null, failure?: () => null): JQueryXHR {
                        jrpc.execute("CLASSI.lookup", [params.data], success);
                        return undefined;
                    }
                },
                placeholder: "",
                allowClear: true,
                minimumInputLength: 3
            };
            this.init(options);
        }
    }

    export class LJSASelAttivita extends WUX.WSelect2 {

        constructor(id?: string, multiple?: boolean) {
            super(id, [], multiple);
            this.name = 'LJSASelAttivita';
        }

        protected componentDidMount(): void {
            let options: Select2Options = {
                ajax: {
                    dataType: "json",
                    delay: 400,
                    processResults: function (result, params) {
                        return {
                            results: result
                        };
                    },
                    transport: function (params: JQueryAjaxSettings, success?: (data: any) => null, failure?: () => null): JQueryXHR {
                        jrpc.execute("ATTIVITA.lookup", [_defService, params.data], success);
                        return undefined;
                    }
                },
                placeholder: "",
                allowClear: true,
                minimumInputLength: 3
            };
            this.init(options);
        }
    }
}