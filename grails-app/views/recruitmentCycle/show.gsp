<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'recruitmentCycle.entity', default: 'RecruitmentCycle List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'RecruitmentCycle List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
<div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
    <btn:listButton onClick="window.location.href='${createLink(controller: 'recruitmentCycle', action: 'list')}'"/>
</div></div>
</div>

<el:hiddenField name="recruitmentCycleId" value="${recruitmentCycle?.id}"/>
<el:row/>
<el:row/>

<br/>
<lay:showWidget size="6" title="">
    <lay:showElement value="${recruitmentCycle?.name}" type="String"
                     label="${message(code: 'recruitmentCycle.name.label', default: 'name')}"/>
    <lay:showElement value="${recruitmentCycle?.currentRecruitmentCyclePhase?.fromDate}" type="ZonedDate"
                     label="${message(code: 'recruitmentCycle.currentRecruitmentCyclePhase.fromDate.label', default: 'startDate')}"/>
</lay:showWidget>
<lay:showWidget size="6" title="">

    <lay:showElement value="${recruitmentCycle?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus}"
                     type="enum"
                     label="${message(code: 'recruitmentCycle.currentRecruitmentCyclePhase.label', default: 'requisitionAnnouncementStatus')}"
                     messagePrefix="EnumRequisitionAnnouncementStatus"/>
    <lay:showElement value="${recruitmentCycle?.currentRecruitmentCyclePhase?.toDate}" type="ZonedDate"
                     label="${message(code: 'recruitmentCycle.currentRecruitmentCyclePhase.toDate.label', default: 'endDate')}"/>
</lay:showWidget>
<el:row/>
<el:row/>
<el:row/>
<br/>

<lay:simpleTab>
    <btn:addButton/>
    <lay:simpleTabElement id="infoTab"  entityName="recruitmentCycle" entityNameAction="show" title="${message(code: 'recruitmentCycle.entity')}" icon="icon-spin2"
                          isActive="true">
        <g:render template="/recruitmentCycle/tabs/infoTab" model="[recruitmentCycle: recruitmentCycle]"/>
    </lay:simpleTabElement>

    <lay:simpleTabElement id="recruitmentCycleTab" tabClassification="recruitmentCycle"
                          entityName="joinedRecruitmentCycleDepartment" entityNameAction="list"
                          title="${message(code: 'recruitmentCycle.joinedRecruitmentCycleDepartment.label', default: 'joinedRecruitmentCycleDepartmentTab')}"
                          icon="icon-commerical-building">
    </lay:simpleTabElement>


    <lay:simpleTabElement id="jobRequisitionTab" entityName="jobRequisition" tabClassification="recruitmentCycle" entityNameAction="list"
                          title="${message(code: 'recruitmentCycle.jobRequisition.label', default: 'jobRequisition')}"
                          icon="icon-th-2"/>

    <lay:simpleTabElement id="vacancyTab" entityName="vacancy" tabClassification="recruitmentCycle" entityNameAction="list"
                          title="${message(code: 'vacancy.entities', default: 'vacancy')}"
                          icon="icon-th-2"/>

    <lay:simpleTabElement id="vacancyAdvertisementsTab" entityName="vacancyAdvertisements" tabClassification="recruitmentCycle" entityNameAction="list"
                          title="${message(code: 'vacancyAdvertisements.entities', default: 'vacancyAdvertisements')}"
                          icon="icon-th-2"/>

    <lay:simpleTabElement id="applicantTab" entityName="applicant" tabClassification="recruitmentCycle" entityNameAction="list"
                          title="${message(code: 'applicant.entities', default: 'applicant')}"
                          icon="icon-th-2"/>

    <lay:simpleTabElement id="interviewTab" entityName="interview" tabClassification="recruitmentCycle" entityNameAction="list"
                          title="${message(code: 'interview.entities', default: 'interview')}"
                          icon="icon-th-2"/>

    <lay:simpleTabElement id="recruitmentCyclePhaseTab" entityName="recruitmentCyclePhase" tabClassification="recruitmentCycle" entityNameAction="list"
                          title="${message(code: 'recruitmentCyclePhase.entities', default: 'RecruitmentCyclePhase')}"
                          icon="icon-statusnet"/>
</lay:simpleTab>
<el:row/>

<script type="text/javascript">
    var entityName = "";
    var tabName = "";
    $('a[data-toggle="tab"]').click(function () {
        $('.alert.page').html('');
        var excludeTab = "infoTab";
        var divId = $(this).attr("href");
        tabName = divId.trim().replace("#", "");

        $(".tab-content").find("div.tab-pane").each(function () {
            var id = $(this).attr('id');
            if (tabName != id && id != excludeTab) {
                $(this).html('');
            }
        });

        entityName = $(this).attr("entityName");

        if (tabName != excludeTab) {
            $.post("${createLink(controller: 'tabs',action: 'loadTab')}", {
                tabName: tabName,
                holderEntityName: 'recruitmentCycle',
                holderEntityId: "${recruitmentCycle?.id}",
                tabEntityName: entityName,
                phaseName: "${recruitmentCycle?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus}"
            }, function (data) {
                $(divId).html(data);
                guiLoading.hide();
                gui.dataTable.initialize($(divId));
                gui.modal.initialize($(divId));
            });
        } else {
            $.ajax({
                url: '${createLink(controller: 'tabs',action: 'showInLine')}',
                type: 'POST',
                data: {
                    id: "${recruitmentCycle?.id}",
                    tabEntityName: "recruitmentCycle"
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
        $.ajax({
            url: '${createLink(controller: 'tabs',action: 'showInLine')}',
            type: 'POST',
            data: {
                id: id,
                withRemoting: true,
                tabEntityName: entityName
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

    function renderInLineEditDepartment() {
        $.ajax({
            url: '${createLink(controller: 'recruitmentCycle',action: 'manageDepartments')}',
            type: 'POST',
            data: {
                'encodedId': "${recruitmentCycle?.encodedId}",
                tabEntityName: entityName,
                isRecruitmentCycleDisabled: true,
                isRelatedObjectTypeDisabled: true,
                isDocumentOwnerDisabled: true,
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
                gui.initAll.init($('#' + entityName + "Div"));
            }
        });
    }

    function renderInLineEdit() {
        $.ajax({
            url: '${createLink(controller: 'tabs',action: 'editInLine')}',
            type: 'POST',
            data: {
                'id': "${recruitmentCycle.id}",
                tabEntityName: entityName,
                isRecruitmentCycleDisabled: true,
                isRelatedObjectTypeDisabled: true,
                isDocumentOwnerDisabled: true
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


    function renderInLineCreate() {
        $.ajax({
            url: '${createLink(controller: 'tabs',action: 'createInLine')}',
            type: 'POST',
            data: {
                'recruitmentCycle.id': "${recruitmentCycle.id}",
                'ownerRecruitmentCycle.id': "${recruitmentCycle.id}",
                'trainee.id': "${recruitmentCycle.id}",
                tabEntityName: entityName,
                isRecruitmentCycleDisabled: true,
                isRelatedObjectTypeDisabled: true,
                isDocumentOwnerDisabled: true,
                %{--relatedObjectType:'${ps.police.pcore.enums.v1.ContactInfoClassification.recruitmentCycle}',--}%
                %{--documentOwner:'${ps.police.pcore.enums.v1.RelatedParty.recruitmentCycle}'--}%

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