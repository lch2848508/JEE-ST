function coltogather(willcheck, colnum)
{
    var alltext = [];
    var togotherNum = [];
    var oldnum = [];
    var id = 1;
    var lasttext = null;
    var rmflag = 1;
    willcheck.each(function ()
    {
        var _rmnum = this.getAttribute('rmnum');
        if (!_rmnum) _rmnum = 0;
        var trdom = jQuery('td:eq(' + (colnum - _rmnum) + ')', this);
        var text = jQuery(trdom).text();
        if (lasttext == null)
        {
            lasttext = text;
        }
        else
        {
            if (lasttext != text)
            {
                togotherNum.push(id);
                lasttext = text;
                id = 1;
            }
            else
            {
                id++;
            }
        }

    });
    togotherNum.push(id);
    jQuery.each(togotherNum, function (i, n)
    {
        oldnum[i] = n;
    });
    var index = 0,
        len = togotherNum.length;
    willcheck.each(function ()
    {
        var _rmnum = this.getAttribute('rmnum');
        if (!_rmnum) _rmnum = 0;
        var tddom = jQuery('td:eq(' + (colnum - _rmnum) + ')', this);
        if (togotherNum[index] == oldnum[index])
        {
            tddom.attr('rowSpan', togotherNum[index]);
            if (togotherNum[index] > 1) togotherNum[index] = togotherNum[index] - 1;
            else index++;
        }
        else
        {
            if (togotherNum[index] == 0)
            {
                index++;
                tddom.attr('rowSpan', togotherNum[index]);
            }
            else
            {
                tddom.remove();
                if (--togotherNum[index] == 0)
                {
                    index++;
                }
            }
            if (_rmnum == 0)
            {
                jQuery(this).attr('rmnum', 1);
            }
            else
            {
                jQuery(this).attr('rmnum', 1 + _rmnum * 1);
            }
        }
    });
    alltext = null;
    togotherNum = null;
    oldnum = null;
}


function isinarr(arr, str)
{
    for (var i = arr.length - 1; i >= 0; i--)
    {
        if (arr[i] == str)
        {
            return i;
        }
    }
    return -1;
}

function checktable(id)
{
    var tdnum = 0;
    $('#' + id + ' tr').each(function ()
    {
        if (tdnum == 0)
        {
            tdnum = $('td', this).size();
        }
        else
        {
            if (tdnum != $('td', this).size())
            {
                tdnum = -1;
                return false;
            }
        }
    });
    if (tdnum > 0) return true;
    return false;
}