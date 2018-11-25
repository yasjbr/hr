<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="title" value="${message(code: 'structure.label', default: 'structure')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<el:validatableForm name="treeForm">
    <el:formGroup>

        <el:autocomplete optionKey="id" optionValue="name" id="firmId"
                         size="3" class=" "
                         controller="firm"
                         action="autocomplete"
                         name="firm.id"
                         label="${message(code: 'firm.label', default: 'firm')}"
                         values="${[[firm?.id, firm?.name]]}"/>
    </el:formGroup>
</el:validatableForm>
<el:row/>
<el:formGroup class="col-md-3">
    <lay:widget icon="icon-flow-tree" title="${g.message(code: "structure.tree.label")}">
        <lay:widgetBody>
            <div class="customHeightTreeView">
                <el:tree multiSelect="false" paramsGenerateFunction="treeParams"
                         id="structureTree" name="structureTree" controller="structure"
                         action="filter" itemKey="id"
                         itemValue="name"/>
            </div>
        </lay:widgetBody>
    </lay:widget>
</el:formGroup>
<el:formGroup class="col-md-9">
    <div id="treeDetailsDiv">
    </div>
</el:formGroup>

<script>
    $('#structureTreeTree').on('selected.fu.tree', function (event, data) {
        // do something with data: { selected: [array], target: [object] }
        var selectedObject = data.selected[0];
        $.ajax({
            url: '${createLink(controller: 'structure',action: 'getDepartmentInfo')}',
            type: 'POST',
            data: {
                encodedId: selectedObject.id,
                isTree:true
            },
            dataType: 'html',
            beforeSend: function (jqXHR, settings) {
                guiLoading.show();
            },
            error: function (jqXHR) {
                guiLoading.hide();
            },
            success: function (html) {
                guiLoading.hide();

                $('#treeDetailsDiv').html(html);
            }
        });


    });


    /**
     * tree params function.
     */
    function treeParams() {
        var searchParams = {};
        searchParams["firm.id"] = $("#firmId").val();
        searchParams["isTree"] = true;
        return searchParams;
    }

    /**
     * initialize the tree of the selected firm.
     * @type {any}
     */
    var $el = $("#firmId");
    $el.on('select2:select', function (evt) {
        var firmId = $("#firmId").val();
        var firmName = $("#firmId option:selected").text();
        window.location = "${g.createLink(controller: "structure",action: "index")}?id=" + firmId;
    });



</script>

<style>
.widget-header-flat {
    background-color: #EFF3F8;
}

.customHeightTreeView {
    height: 350px !important;
    overflow-x: hidden;
    overflow-y: auto;
}

.tab-content {
    height: 375px !important;
    overflow-x: hidden;
    overflow-y: auto;
}
</style>

</body>
</html>