import android.annotation.SuppressLint
import android.os.Build


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.datastructures.chatty.R
import com.example.myapplication.models.UserModel
import kotlinx.android.synthetic.main.story_list_item.view.*

class Statue_Adapter() : androidx.recyclerview.widget.ListAdapter<UserModel, Statue_Adapter.viewHolder>(Diff()) {

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
                Glide.with(context).load(current.stories?.last()?.url).into(myStoryImg)

            } catch (e : Exception){

            }
        }


    }



    private var onItemClickListner: ((UserModel) -> Unit)? = null

    fun setOnItemClickListner(listner: (UserModel) -> Unit) {
        onItemClickListner = listner
    }


}


class Diff() : DiffUtil.ItemCallback<UserModel>() {
    override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
        return oldItem.imageUri == newItem.imageUri
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
        return oldItem == newItem
    }

}