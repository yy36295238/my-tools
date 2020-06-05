<style scoped>
.left20 {
    margin-left: 20px;
}
.top20 {
    margin-top: 20px;
}
.pad20 {
    padding: 20px;
}
</style>
<template>
    <div>
        <Layout>
            <Content :style="{ background: '#fff' }">
                <div class="pad20">
                    <Form>
                        <Row>
                            <Col>
                            <FormItem>
                                <Row :gutter="20">
                                    <Col span="3">
                                    <Input v-model="formData.className" placeholder="名称" />
                                    </Col>
                                    <Col span="4">
                                    <Button type="primary" icon="md-search" @click="query()">查询</Button>
                                    <Button type="success" icon="md-add" @click="showAdd()" class="left20">添加</Button>
                                    </Col>
                                </Row>
                                <Table :columns="columns" :data="data" class="top20">
                                </Table>
                            </FormItem>
                            </Col>
                        </Row>
                    </Form>
                </div>
            </Content>
        </Layout>
        <!-- 抽屉 -->
        <Drawer title="添加" v-model="addShow" width="720" :mask-closable="false" :styles="styles" placement="right">
            <Form :model="formData">
                _addFormData_
            </Form>
            <div class="demo-drawer-footer">
                <Button style="margin-right: 8px" @click="addShow = false">取消</Button>
                <Button type="primary" @click="handleSave()">提交</Button>
            </div>
        </Drawer>
    </div>
</template>
<script>
export default {
    components: {
    },
    data() {
        return {
            url: "http://localhost:6161",
            addShow: false,
            styles: {
                height: 'calc(100% - 55px)',
                overflow: 'auto',
                paddingBottom: '53px',
                position: 'static'
            },
            _formDataJson_
            columns: this.columnsData(),
            data: [],
        };
    },
    mounted() {
        this.query();
    },
    methods: {
        query() {
            this.axios.get(this.url + "_api_/list").then(res => {
                this.data = res.data.data;
            });
        },
        showAdd(row) {
            if (row) {
                this.formData.id = row.id;
                this.formData.className = row.className;
                this.formData.revert = row.revert;
            } else {
                this.formData.className = undefined;
                this.formData.revert = undefined;
            }
            this.addShow = true;
        },
        handleSave() {
            var uri = "_api_/" + (this.formData.id ? "updateById" : "add");
            this.formData.appId = this.appId;
            this.basePostRequest(this.url + uri, this.formData);
        },
        handleDelete(id) {
            this.basePostRequest(this.url + "_api_/deleteById", { id, id });
        },
        basePostRequest(url, data) {
            this.axios.post(url, data).then(res => {
                this.$Notice.success({
                    title: res.data.message
                });
                this.query();
                this.addShow = false;
            });
        },
        columnsData() {
            return [
                {
                    align: "center",
                    render: (h, params) => {
                        return h('div', [
                            h('Tooltip', {
                                props: {
                                    content: '编辑',
                                    placement: 'top',
                                },
                            },
                                [h('Icon', {
                                    props: {
                                        type: 'ios-create-outline',
                                        size: 20,
                                        color: "#2d8cf0"
                                    },
                                    style: {
                                        'margin-left': '15px',
                                        'cursor': 'pointer'
                                    },
                                    on: {
                                        click: () => {
                                            this.showAdd(params.row);
                                        }
                                    }
                                }),]),
                            h('Tooltip', {
                                props: {
                                    content: '删除',
                                    placement: 'top',
                                },
                            }, [h('Icon', {
                                props: {
                                    type: 'md-close',
                                    size: 20,
                                    color: "#ed4014"
                                },
                                style: {
                                    'margin-left': '15px',
                                    'cursor': 'pointer'
                                },
                                on: {
                                    click: () => {
                                        this.handleDelete(params.row.id);
                                    }
                                }
                            }),]),
                        ]);
                    },
                    width: 200
                },
                _columnsDataJson_
            ]
        }
    }
};
</script>