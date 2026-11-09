package cn.edu.sicnu.myapplication4.game

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 翻牌游戏适配器：把 [Card] 列表绑定到 [PlayingCardView]。
 * 点击回调由适配器统一转发给控制器。
 */
class PlayingCardAdapter(
    private val onCardClicked: (Int) -> Unit
) : RecyclerView.Adapter<PlayingCardAdapter.CardViewHolder>() {

    private val cards = mutableListOf<Card>()

    companion object {
        /** 牌与牌之间的水平间距。 */
        private const val CARD_GAP_HORIZONTAL_DP = 8f

        /** 牌与牌之间的垂直间距。 */
        private const val CARD_GAP_VERTICAL_DP = 2f
    }

    /** 刷新牌面数据。 */
    fun submitCards(newCards: List<Card>) {
        cards.clear()
        cards.addAll(newCards)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val density = parent.resources.displayMetrics.density
        val horizontalGap = (density * CARD_GAP_HORIZONTAL_DP).toInt()
        val verticalGap = (density * CARD_GAP_VERTICAL_DP).toInt()
        val cardView = PlayingCardView(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(horizontalGap, verticalGap, horizontalGap, verticalGap)
            }
        }
        return CardViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position], position, onCardClicked)
    }

    override fun getItemCount(): Int = cards.size

    class CardViewHolder(
        private val cardView: PlayingCardView
    ) : RecyclerView.ViewHolder(cardView) {

        fun bind(card: Card, position: Int, onCardClicked: (Int) -> Unit) {
            cardView.tag = "card_$position"
            cardView.bind(card, position) { onCardClicked(position) }
        }
    }
}
