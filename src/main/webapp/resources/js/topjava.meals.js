const mealAjaxUrl = "profile/meals/";
let filterForm = $('#filter');

// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: mealAjaxUrl,
    updateTableWithFilter() {
        $.ajax({
            type: "GET",
            url: ctx.ajaxUrl + "filter",
            data: filterForm.serialize()
        }).done(function (data) {
            ctx.datatableApi.clear().rows.add(data).draw();
        });
    }
};

// $(document).ready(function () {
$(function () {
    makeEditable(
        $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime"
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false
                }
            ],
            "order": [
                [
                    0,
                    "desc"
                ]
            ]
        })
    );
});

function clearFilter() {
    filterForm[0].reset();
    updateTable();
}

$(function () {
    $.datetimepicker.setLocale('ru');
    $('#startDate').datetimepicker({
        timepicker: false,
        format: "Y-m-d"
    });
    $('#endDate').datetimepicker({
        timepicker: false,
        format: "Y-m-d",
    });
    $('#startTime').datetimepicker({
        datepicker: false,
        format: "H:i"
    });
    $('#endTime').datetimepicker({
        datepicker: false,
        format: "H:i"
    })
});