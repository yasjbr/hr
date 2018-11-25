<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'evaluationSection.entity', default: 'EvaluationSection List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'EvaluationSection List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'evaluationSection',action:'list')}'"/>
    </div></div>
</div>
<el:row/>
<br/>
<lay:simpleTab>
    <lay:simpleTabElement id="evaluationSectionTab" entityName="evaluationSection" entityNameAction="show"
                          title="${message(code: 'evaluationSection.entity')}"
                          icon="icon-th-4" tabClassification="evaluationSection"
                          isActive="true">
        <g:render template="/evaluationSection/show" model="[evaluationSection: evaluationSection]"/>
    </lay:simpleTabElement>


    <lay:simpleTabElement id="evaluationItemTab" tabClassification="evaluationSection"
                          entityName="evaluationItem" entityNameAction="list"
                          title="${message(code: 'evaluationItem.entities', default: 'evaluationItem')}"
                          icon="icon-list-add">
    </lay:simpleTabElement>
</lay:simpleTab>




<script type="text/javascript">
    var entityName = "";
    var commandName = "";
    var tabName = "";
    var tabClassification = "";
    $('a[data-toggle="tab"]').click(function () {
        $('.alert.page').html('');
        var excludeTab = "evaluationSectionTab";
        var divId = $(this).attr("href");
        tabName = divId.trim().replace("#", "");

        $(".tab-content").find("div.tab-pane").each(function () {
            var id = $(this).attr('id');
            if (tabName != id && id != excludeTab) {
                $(this).html('');
            }
        });
        entityName = $(this).attr("entityName");
        commandName = $(this).attr("commandName");
        tabClassification = $(this).attr("tabClassification");
        var url = null;
        var holderEntityId = null;
        var holderEntityPath = null;
        if (tabName != excludeTab) {

            url = "${createLink(controller: 'tabs',action: 'loadTab')}";
            holderEntityId = "${evaluationSection?.id}";
            $.post(url, {
                tabName: tabName,
                holderEntityName: tabClassification,
                holderEntityPath: holderEntityPath,
                holderEntityId: holderEntityId,
                tabEntityName: entityName,
                commandName: commandName,
            }, function (data) {
                $(divId).html(data);
                guiLoading.hide();
                gui.dataTable.initialize($(divId));
                gui.modal.initialize($(divId));
            });
        } else {
            var id = "";

            url = "${createLink(controller: 'tabs',action: 'showInLine')}";
            id = "${evaluationSection?.id}";

            $.ajax({
                url: url,
                type: 'POST',
                data: {
                    id: id,
                    tabEntityName: entityName,
                    withRemoting: false
                },
                dataType: 'html',
                beforeSend: function (jqXHR, settings) {
                    $('.alert.page').html('');
                    guiLoading.show();
                },
                error: function (jqXHR) {
                    guiLoading.hide();
                },
                success: function (data) {
                    guiLoading.hide();
                    $(divId).html(data);

                }
            });
        }
    });

    function renderInLineShow(id) {
        var justAddress = null;
        var withRemoting = false;
        var url;

        url = "${createLink(controller: 'tabs',action: 'showInLine')}";

        $.ajax({
            url: url,
            type: 'POST',
            data: {
                id: id,
                tabEntityName: entityName,
                withRemoting: withRemoting,
                justAddress: justAddress
            },
            dataType: 'html',
            beforeSend: function (jqXHR, settings) {
                $('.alert.page').html('');
                guiLoading.show();
            },
            error: function (jqXHR) {
                guiLoading.hide();
            },
            success: function (data) {
                guiLoading.hide();
                $('#' + entityName + "Div").html(data);

            }
        });
    }

    function renderInLineEdit(id) {
        var justAddress = null;
        var withRemoting = false;

        var url;

        url = "${createLink(controller: 'tabs',action: 'editInLine')}";

        $.ajax({
            url: url,
            type: 'POST',
            data: {
                id: id,
                tabEntityName: entityName,
                isPersonDisabled: true,
                isEmployeeDisabled: true,
                isRelatedObjectTypeDisabled: true,
                justAddress: justAddress,
                isDocumentOwnerDisabled: true,
                withRemoting: withRemoting
            },
            dataType: 'html',
            beforeSend: function (jqXHR, settings) {
                $('.alert.page').html('');
                guiLoading.show();

            },
            error: function (jqXHR) {
                guiLoading.hide();
            },
            success: function (data) {
                guiLoading.hide();
                $('#' + entityName + "Div").html(data);

            }
        });
    }

    function renderInLineList() {
        $('.alert.page').html('');
        guiLoading.show();
        $('a[href="#' + tabName + '"]').trigger("click");
    }

</script>



</body>
</html>