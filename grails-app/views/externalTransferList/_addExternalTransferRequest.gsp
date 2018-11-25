<el:dataTable id="externalTransferRequestTableToChooseInExternalTransfer"
              searchFormName="addExternalTransferRequestFormInExternalTransfer"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="externalTransferRequest"
              action="filter"
              spaceBefore="true"
              hasRow="true" domainColumns="DOMAIN_COLUMNS"
              serviceName="externalTransferRequest">
</el:dataTable>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));
    gui.modal.initialize($('#application-modal-main-content'));
</script>
