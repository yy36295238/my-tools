<style scoped>.left20 {    margin-left: 20px;}.top20 {    margin-top: 20px;}.pad20 {    padding: 20px;}</style><template>    <div>        <Layout>            <Content :style="{ background: '#fff' }">                <div class="pad20">                    <Form>                        <Row>                            <Col>                            <FormItem>                                <Row :gutter="20">                                    <Col span="3">                                    <Input v-model="formData.className" placeholder="名称" />                                    </Col>                                    <Col span="4">                                    <Button type="primary" icon="md-search" @click="query()">查询</Button>                                    <Button type="success" icon="md-add" @click="showAdd()" class="left20">添加</Button>                                    </Col>                                </Row>                                <Table :columns="columns" :data="data" class="top20">                                </Table>                            </FormItem>                            </Col>                        </Row>                    </Form>                </div>            </Content>        </Layout>        <!-- 抽屉 -->        <Drawer title="添加" v-model="addShow" width="720" :mask-closable="false" :styles="styles" placement="right">            <Form :model="formData">                <Row :gutter="20">
	<Col span="12">
      <FormItem label="主键" label-position="top">
           <Input v-model="formData.id" placeholder="主键" />
       </FormItem>
     </Col>
	<Col span="12">
      <FormItem label="名称" label-position="top">
           <Input v-model="formData.name" placeholder="名称" />
       </FormItem>
     </Col>
</Row>
<Row :gutter="20">
	<Col span="12">
      <FormItem label="金额" label-position="top">
           <Input v-model="formData.amount" placeholder="金额" />
       </FormItem>
     </Col>
	<Col span="12">
      <FormItem label="文本" label-position="top">
           <Input v-model="formData.text" placeholder="文本" />
       </FormItem>
     </Col>
</Row>
<Row :gutter="20">
	<Col span="12">
      <FormItem label="状态" label-position="top">
           <Input v-model="formData.status" placeholder="状态" />
       </FormItem>
     </Col>
	<Col span="12">
      <FormItem label="创建时间" label-position="top">
           <Input v-model="formData.createTime" placeholder="创建时间" />
       </FormItem>
     </Col>
</Row>
            </Form>            <div class="demo-drawer-footer">                <Button style="margin-right: 8px" @click="addShow = false">取消</Button>                <Button type="primary" @click="handleSave()">提交</Button>            </div>        </Drawer>    </div></template><script>export default {    components: {    },    data() {        return {            url: "http://localhost:8080",            addShow: false,            styles: {                height: 'calc(100% - 55px)',                overflow: 'auto',                paddingBottom: '53px',                position: 'static'            },		formData: {
			id: undefined,
			name: undefined,
			amount: undefined,
			text: undefined,
			status: undefined,
			createTime: undefined,
		},            columns: this.columnsData(),            data: [],        };    },    mounted() {        this.query();    },    methods: {        query() {            this.axios.get(this.url + "/api/v1/customer/list").then(res => {                this.data = res.data.data;            });        },        showAdd(row) {		if (row) {
			this.formData.id = row.id;
			this.formData.name = row.name;
			this.formData.amount = row.amount;
			this.formData.text = row.text;
			this.formData.status = row.status;
			this.formData.createTime = row.createTime;
		} else {
			this.formData.id = undefined;
			this.formData.name = undefined;
			this.formData.amount = undefined;
			this.formData.text = undefined;
			this.formData.status = undefined;
			this.formData.createTime = undefined;
		}
            this.addShow = true;        },        handleSave() {            var uri = "/api/v1/customer/" + (this.formData.id ? "updateById" : "add");            this.formData.appId = this.appId;            this.basePostRequest(this.url + uri, this.formData);        },        handleDelete(row) {            this.basePostRequest(this.url + "/api/v1/customer/deleteById", { id, row.id });        },        basePostRequest(url, data) {            this.axios.post(url, data).then(res => {                this.$Notice.success({                    title: res.data.message                });                this.query();                this.addShow = false;            });        },        columnsData() {            return [                {                    align: "center",                    render: (h, params) => {                        return h('div', [                            h('Tooltip', {                                props: {                                    content: '编辑',                                    placement: 'top',                                },                            },                                [h('Icon', {                                    props: {                                        type: 'ios-create-outline',                                        size: 20,                                        color: "#2d8cf0"                                    },                                    style: {                                        'margin-left': '15px',                                        'cursor': 'pointer'                                    },                                    on: {                                        click: () => {                                            this.showAdd(params.row);                                        }                                    }                                }),]),                            h('Tooltip', {                                props: {                                    content: '删除',                                    placement: 'top',                                },                            }, [h('Icon', {                                props: {                                    type: 'md-close',                                    size: 20,                                    color: "#ed4014"                                },                                style: {                                    'margin-left': '15px',                                    'cursor': 'pointer'                                },                                on: {                                    click: () => {                                        this.handleDelete(params.row);                                    }                                }                            }),]),                        ]);                    },                    width: 200                },		{
			title: "主键",
			key: "id",
		},
		{
			title: "名称",
			key: "name",
		},
		{
			title: "金额",
			key: "amount",
		},
		{
			title: "文本",
			key: "text",
		},
		{
			title: "状态",
			key: "status",
		},
		{
			title: "创建时间",
			key: "createTime",
		},
            ]        }    }};</script>