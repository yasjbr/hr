<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'interview.entity', default: 'Interview List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'Interview List')}" />
    <title>${title}</title>
</head>
<body>


<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'interview', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<el:row/>
<lay:simpleTab>

    <lay:simpleTabElement id="interviewTab" entityName="interview" entityNameAction="show" title="${message(code: 'interview.label')}" icon="icon-commerical-building" isActive="true" >
        <g:render template="/interview/show" model="[interview: interview]"/>

    </lay:simpleTabElement>

        <lay:simpleTabElement id="applicantTab"
                              entityName="applicant" entityNameAction="list"
                              title="${message(code: 'interview.applicant.label', default: 'applicants')}"
                              icon="icon-doc-3">
        </lay:simpleTabElement>

</lay:simpleTab>

<el:row />

<script type="text/javascript">
    var entityName = "";
    var tabName = "";
    $('a[data-toggle="tab"]').click(function () {
        $('.alert.page').html('');
        var excludeTab = "interviewTab";
        var divId = $(this).attr("href");
        tabName = divId.trim().replace("#","");

        $(".tab-content").find("div.tab-pane").each( function(){
            var id = $(this).attr('id');
            if(tabName != id && id != excludeTab){
                $(this).html('');
            }
        });

        entityName = $(this).attr("entityName");
        if(tabName != excludeTab) {
            $.post("${createLink(controller: 'tabs',action: 'loadTab')}", {
                tabName: tabName,
                holderEntityName: 'interview',
                holderEntityId: "${interview?.id}",   withRemoting:"true", interviewStatus:"${interview?.interviewStatus}",
                tabEntityName: entityName
            }, function (data) {
                $(divId).html(data);
                guiLoading.hide();
                gui.dataTable.initialize($(divId));
                gui.modal.initialize($(divId));
            });
        }else{
            $.ajax({
                url: '${createLink(controller: 'tabs',action: 'showInLine')}',
                type: 'POST',
                data: {
                    id: "${interview?.id}",
                    tabEntityName: "interview",
                    withRemoting:"true"
                },
                dataType: 'html',
                beforeSend: function(jqXHR,settings) {
                    $('.alert.page').html('');
                    guiLoading.show();
                },
                error: function(jqXHR) {
                    guiLoading.hide();
                },
                success: function(data) {
                    guiLoading.hide();
                    $(divId).html(data);

                }
            });
        }
    });



    function renderInLineShow(id) {
        $.ajax({
            url: '${createLink(controller: 'tabs',action: 'showInLine')}',
            type: 'POST',
            data: {
                id: id,
                withRemoting:"true",
                tabEntityName: entityName
            },
            dataType: 'html',
            beforeSend: function(jqXHR,settings) {
                $('.alert.page').html('');
                guiLoading.show();
            },
            error: function(jqXHR) {
                guiLoading.hide();
            },
            success: function(data) {
                guiLoading.hide();
                $('#'+entityName+"Div").html(data);

            }
        });
    }

    function renderInLineEdit(id) {
        $.ajax({
            url: '${createLink(controller: 'tabs',action: 'editInLine')}',
            type: 'POST',
            data: {
                id: id,
                interviewId:$("#interviewId").val(),
                withRemoting:"true",
                tabEntityName:entityName
            },
            dataType: 'html',
            beforeSend: function(jqXHR,settings) {
                $('.alert.page').html('');
                guiLoading.show();

            },
            error: function(jqXHR) {
                guiLoading.hide();
            },
            success: function(data) {
                guiLoading.hide();
                $('#'+entityName+"Div").html(data);

            }
        });
    }
    function renderInLineCreate() {
        $.ajax({
            url: '${createLink(controller: 'tabs',action: 'createInLine')}',
            type: 'POST',
            data: {
                'interview.id': "${interview.id}",
                'ownerinterview.id': "${interview.id}",
                'trainee.id': "${interview.id}",
                tabEntityName:entityName,
                isinterviewDisabled:true,
                isRelatedObjectTypeDisabled:true,
                isDocumentOwnerDisabled:true
            },
            dataType: 'html',
            beforeSend: function(jqXHR,settings) {
                $('.alert.page').html('');
                guiLoading.show();
            },
            error: function(jqXHR) {
                guiLoading.hide();
            },
            success: function(data) {
                guiLoading.hide();
                $('#'+entityName+"Div").html(data);

            }
        });
    }

    function renderInLineList() {
        $('.alert.page').html('');
        guiLoading.show();
        $('a[href="#'+tabName+'"]').trigger("click");
    }

</script>

</body>
</html>

