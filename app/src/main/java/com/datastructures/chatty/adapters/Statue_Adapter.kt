import android.annotation.SuppressLint
import android.os.Build
import com.datastructures.chatty.models.UserStatue


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.datastructures.chatty.R
import kotlinx.android.synthetic.main.story_list_item.view.*

class Statue_Adapter() : androidx.recyclerview.widget.ListAdapter<UserStatue, Statue_Adapter.viewHolder>(Diff()) {

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        return viewHolder(LayoutInflater.from(parent.context).inflate(R.layout.story_list_item, parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val current = getItem(position)



        holder.itemView.apply {

            setOnClickListener {

                onItemClickListner?.let {
                    it(current)
                }
            }





            myStoryTitle.text = current.name
            myLastStory.text = current.lastStory

            try {
                Glide.with(this).load(current.stories?.last()?.url).into(myStoryImg)

            } catch (e : Exception){

            }
        }


    }



    private var onItemClickListner: ((UserStatue) -> Unit)? = null

    fun setOnItemClickListner(listner: (UserStatue) -> Unit) {
        onItemClickListner = listner
    }


}


class Diff() : DiffUtil.ItemCallback<UserStatue>() {
    override fun areItemsTheSame(oldItem: UserStatue, newItem: UserStatue): Boolean {
        return oldItem.myImg == newItem.myImg
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: UserStatue, newItem: UserStatue): Boolean {
        return oldItem == newItem
    }

}