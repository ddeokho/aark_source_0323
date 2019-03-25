package automacticphone.android.com.casebook.activity.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;
import com.unnamed.b.atv.model.TreeNode;

import java.util.ArrayList;
import java.util.Random;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.data.CaseData;
import automacticphone.android.com.casebook.activity.data.SubTreeCaseData;

public class SubTreeViewHolder extends TreeNode.BaseNodeViewHolder<SubTreeViewHolder.SubTreeItem> {

    private int contentCount;
    public SubTreeViewHolder(Context context, int count ) {
        super(context);

        contentCount = count;
    }

    @Override
    public View createNodeView(TreeNode node, SubTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_subtree_node, null, false);

        TextView userNameLabel = (TextView) view.findViewById(R.id.username);
        userNameLabel.setText( value.title );

        return view;
    }

    @Override
    public void toggle(boolean active) {
    }


    public static class SubTreeItem {
        public int index;
        public String title;
        public SubTreeCaseData subTreeCaseData = null;
        public SubTreeItem(int _index, String _t, SubTreeCaseData subTreeCaseData)
        {
            this.index = _index;
            this.title = _t;
            this.subTreeCaseData = subTreeCaseData;
        }
        // rest will be hardcoded
    }

}
