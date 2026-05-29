// screen-new-month.jsx — Finance OS — "New month" flow surfaces.
// Three pieces:
//   1. UnallocatedMonthBanner — drop-in replacement for BudgetMonthCard
//      on Home when the current month has no allocation in DB.
//   2. HomeWithBanner — full Home screen showing the banner in place of
//      the regular Budget card. All other cards keep their normal style.
//   3. AllocationStep1NewMonth + AllocationTemplatePicker — variations
//      of the existing AllocationScreen for the new-month context.

// ────────────────────────────────────────────────────────────
// 1. Banner card — dashed border, primary gold accent.
//    Same outer dimensions and rhythm as BudgetMonthCard so it
//    slots in without disrupting the layout grid.
// ────────────────────────────────────────────────────────────
function UnallocatedMonthBanner({ monthLabel = 'Juin 2026', onAllocate }) {
  return (
    <div style={{
      background: `linear-gradient(180deg, ${FOS.primary}0F 0%, transparent 60%)`,
      border: `1.5px dashed ${FOS.primary}66`,
      borderRadius: 20, padding: 18,
      display: 'flex', flexDirection: 'column', gap: 14,
      position: 'relative', overflow: 'hidden',
    }}>
      {/* Soft glow blob top-right — keeps the empty card from feeling flat,
          without veering into "alert" territory. */}
      <div style={{
        position: 'absolute', top: -40, right: -40,
        width: 120, height: 120, borderRadius: '50%',
        background: `${FOS.primary}1F`, filter: 'blur(28px)',
        pointerEvents: 'none',
      }} />

      <div style={{ display: 'flex', alignItems: 'center', gap: 12, position: 'relative' }}>
        <div style={{
          width: 44, height: 44, borderRadius: 12,
          background: `${FOS.primary}1F`, color: FOS.primary,
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          border: `1px solid ${FOS.primary}33`,
        }}>
          <Icon name="calendar-plus" size={22} strokeWidth={1.8} />
        </div>
        <div style={{ flex: 1, minWidth: 0 }}>
          <div style={{
            fontFamily: 'DM Sans', fontSize: 11, fontWeight: 600,
            color: FOS.primary, textTransform: 'uppercase', letterSpacing: 1.4,
          }}>Nouveau mois</div>
          <div style={{
            fontFamily: 'DM Sans', fontSize: 16, fontWeight: 600,
            color: FOS.text, marginTop: 2, lineHeight: 1.3,
          }}>
            {monthLabel} n'est pas encore alloué
          </div>
        </div>
      </div>

      <div style={{
        fontFamily: 'DM Sans', fontSize: 13, color: FOS.textDim,
        lineHeight: 1.5, position: 'relative',
      }}>
        Alloue ton mois pour suivre ton budget. Tes enveloppes du mois
        précédent peuvent être reprises en un tap.
      </div>

      <button onClick={onAllocate} style={{
        alignSelf: 'flex-start',
        display: 'inline-flex', alignItems: 'center', gap: 6,
        padding: '12px 18px', borderRadius: 14,
        background: FOS.primary, color: FOS.onPrimary, border: 'none',
        fontFamily: 'DM Sans', fontSize: 14, fontWeight: 700,
        cursor: 'pointer', letterSpacing: 0.2,
        position: 'relative',
      }}>
        Allouer le mois
        <Icon name="arrow-right" size={15} strokeWidth={2.4} />
      </button>
    </div>
  );
}

// ────────────────────────────────────────────────────────────
// 2. Home screen with the banner in place of BudgetMonthCard.
//    Top section (greeting, net-worth hero) reuses the same content as
//    HomeScreen so the swap is contextual — the only thing missing is
//    the current month's budget data, which is exactly what the banner
//    asks the user to provide.
// ────────────────────────────────────────────────────────────
function HomeWithUnallocatedBanner({ onAllocate, showDesignNote = false }) {
  const monthLabel = 'Juin 2026';
  const nw = CURRENT.netWorth;

  return (
    <div style={{ flex: 1, overflow: 'auto', paddingBottom: 100 }}>
      {/* Greeting */}
      <div style={{
        padding: '8px 20px 4px',
        display: 'flex', justifyContent: 'space-between', alignItems: 'center',
      }}>
        <div>
          <div style={{ fontFamily: 'DM Sans', fontSize: 13, color: FOS.textDim }}>Bonjour, Théo</div>
          <div style={{ fontFamily: 'DM Sans', fontSize: 18, fontWeight: 600, color: FOS.text }}>{monthLabel}</div>
        </div>
        <div style={{
          width: 40, height: 40, borderRadius: 100,
          background: FOS.surface, border: `1px solid ${FOS.outline}`,
          display: 'flex', alignItems: 'center', justifyContent: 'center', color: FOS.text,
          position: 'relative',
        }}>
          <Icon name="bell" size={18} />
          {/* Notification dot — corresponds to the "new month" notification
              described in the brief. Same hue as the banner accent. */}
          <div style={{
            position: 'absolute', top: 8, right: 9,
            width: 8, height: 8, borderRadius: 100,
            background: FOS.primary, border: `2px solid ${FOS.surface}`,
          }} />
        </div>
      </div>

      {/* Hero net-worth card — unchanged: patrimoine accrues regardless of
          whether the current month is allocated. */}
      <div style={{ padding: '12px 20px 0' }}>
        <Card style={{ padding: 20 }}>
          <SectionLabel>Patrimoine total</SectionLabel>
          <div style={{ display: 'flex', alignItems: 'baseline', gap: 10, marginTop: 8 }}>
            <Num size={40} weight={500} style={{ letterSpacing: -1.2 }}>
              {nw.toLocaleString('fr-FR')}
            </Num>
            <Num size={20} weight={400} color={FOS.textDim}>€</Num>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginTop: 8 }}>
            <Pill color={FOS.primary} bg={`${FOS.primary}1F`}>
              <svg width="10" height="10" viewBox="0 0 10 10" style={{ marginRight: 4 }}>
                <path d="M5 1l4 5H1z" fill={FOS.primary}/>
              </svg>
              <Num size={12} color={FOS.primary} weight={600}>+{CURRENT.netWorthDelta.toLocaleString('fr-FR')} €</Num>
              <span style={{ marginLeft: 4, opacity: 0.7 }}>30j</span>
            </Pill>
            <span style={{ fontFamily: 'DM Sans', fontSize: 12, color: FOS.textDim }}>
              Mai clôturé
            </span>
          </div>
        </Card>
      </div>

      {/* Banner — replaces BudgetMonthCard for this state */}
      <div style={{ padding: '14px 20px 0' }}>
        <SectionLabel>Budget {monthLabel.split(' ')[0].toLowerCase()}</SectionLabel>
        <div style={{ marginTop: 8 }}>
          <UnallocatedMonthBanner monthLabel={monthLabel} onAllocate={onAllocate} />
        </div>
      </div>

      {/* Last month recap — keeps the screen useful while the user decides.
          Closed previous month visible as a confidence anchor. */}
      <div style={{ padding: '20px 20px 0' }}>
        <SectionLabel>Dernier mois clôturé</SectionLabel>
        <Card variant="variant" style={{
          marginTop: 8, padding: '14px 16px',
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        }}>
          <div>
            <div style={{ fontFamily: 'DM Sans', fontSize: 14, color: FOS.text, fontWeight: 500, textTransform: 'capitalize' }}>
              Mai 2026
            </div>
            <div style={{ fontFamily: 'DM Sans', fontSize: 11, color: FOS.textDim, marginTop: 2 }}>
              Revenu <Num size={11} color={FOS.textDim}>4 200 €</Num> · Dépenses <Num size={11} color={FOS.textDim}>2 960 €</Num>
            </div>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
            <div style={{ textAlign: 'right' }}>
              <div style={{ fontFamily: 'DM Sans', fontSize: 10, color: FOS.textDim, textTransform: 'uppercase', letterSpacing: 0.8 }}>Patrimoine</div>
              <Num size={14} color={FOS.pos} weight={500}>+1 840 €</Num>
            </div>
            <div style={{ width: 8, height: 8, borderRadius: 100, background: FOS.primary }} />
          </div>
        </Card>
      </div>

      {/* Design note — visual logic guide for the reviewer, not part of the UI */}
      {showDesignNote && (
        <DesignNote>
          <strong>Logique de transition :</strong> au lancement, l'app vérifie si
          le mois courant (<code>YYYY-MM</code>) existe en base. Si non → cette
          bannière. Si oui → BudgetMonthCard normal.
        </DesignNote>
      )}
    </div>
  );
}

// ────────────────────────────────────────────────────────────
// 3a. Allocation — Step 1 (income), labelled "Étape 1 sur 2"
//      for a first-time / new-month flow (no template step).
// ────────────────────────────────────────────────────────────
function AllocationStep1NewMonth() {
  const [income, setIncome] = React.useState('4200');
  const monthLabel = 'Juin 2026';

  return (
    <div style={{ flex: 1, display: 'flex', flexDirection: 'column', minHeight: 0 }}>
      <ScreenHeader
        title={`Allouer ${monthLabel.toLowerCase()}`}
        onBack={() => {}}
        right={
          <div style={{ display: 'flex', gap: 4 }}>
            {[0, 1].map(i => (
              <div key={i} style={{
                width: i === 0 ? 20 : 6, height: 6, borderRadius: 100,
                background: i === 0 ? FOS.primary : FOS.surfaceVar,
              }} />
            ))}
          </div>
        }
      />

      <div style={{ flex: 1, minHeight: 0, overflow: 'auto', paddingBottom: 130 }}>
        <div style={{ padding: '8px 20px 0' }}>
          <div style={{ fontFamily: 'DM Sans', fontSize: 13, color: FOS.textDim, marginTop: 8 }}>
            Étape 1 sur 2
          </div>
          <div style={{
            fontFamily: 'DM Sans', fontSize: 22, fontWeight: 600,
            color: FOS.text, marginTop: 4, lineHeight: 1.3,
          }}>
            Quel est ton revenu ce mois ?
          </div>

          <div style={{ marginTop: 40, textAlign: 'center' }}>
            <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'center', gap: 6 }}>
              <input
                value={income}
                onChange={(e) => setIncome(e.target.value.replace(/[^0-9]/g, ''))}
                inputMode="numeric"
                style={{
                  width: 220, textAlign: 'center',
                  background: 'transparent', border: 'none', outline: 'none',
                  fontFamily: '"Geist Mono", monospace',
                  fontSize: 56, fontWeight: 500, color: FOS.text, letterSpacing: -2,
                  caretColor: FOS.primary,
                }}
              />
              <Num size={28} color={FOS.textDim}>€</Num>
            </div>
            <div style={{
              margin: '8px auto 0', width: 160, height: 1,
              background: `linear-gradient(90deg, transparent, ${FOS.primary}, transparent)`,
            }} />
          </div>

          <div style={{ marginTop: 28, display: 'flex', gap: 8, flexWrap: 'wrap', justifyContent: 'center' }}>
            {['4 000', '4 200', '4 500', '5 000'].map(v => {
              const num = v.replace(/\s/g, '');
              const active = num === income;
              return (
                <button key={v} onClick={() => setIncome(num)} style={{
                  padding: '10px 16px', borderRadius: 100,
                  background: active ? `${FOS.primary}1F` : FOS.surfaceVar,
                  color: active ? FOS.primary : FOS.text,
                  border: active ? `1px solid ${FOS.primary}` : `1px solid ${FOS.outlineSt}`,
                  fontFamily: '"Geist Mono", monospace', fontSize: 13, cursor: 'pointer',
                }}>{v} €</button>
              );
            })}
          </div>

          {/* New-month context message — explicit "first time" framing,
              hinting that the next step will fill from previous month
              (but skip the template picker). */}
          <div style={{
            marginTop: 40, padding: 14,
            background: `${FOS.primary}0F`, border: `1px solid ${FOS.primary}26`,
            borderRadius: 14,
            display: 'flex', gap: 12, alignItems: 'flex-start',
          }}>
            <div style={{
              width: 28, height: 28, borderRadius: 100,
              background: `${FOS.primary}1F`, color: FOS.primary,
              display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0,
            }}>
              <Icon name="sparkles" size={14} strokeWidth={1.8} />
            </div>
            <div style={{
              fontFamily: 'DM Sans', fontSize: 12.5, color: FOS.text, lineHeight: 1.5,
            }}>
              On reprend ton allocation de <strong>mai 2026</strong>. Tu
              pourras ajuster ou retirer des enveloppes à l'étape suivante.
            </div>
          </div>
        </div>
      </div>

      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 0,
        background: FOS.surfaceCt, borderTop: `1px solid ${FOS.outline}`,
        padding: '14px 20px 18px',
      }}>
        <button style={{
          width: '100%', padding: '16px', borderRadius: 16,
          background: FOS.primary, color: FOS.onPrimary, border: 'none',
          fontFamily: 'DM Sans', fontWeight: 700, fontSize: 15,
          cursor: 'pointer', letterSpacing: 0.3,
        }}>Continuer</button>
      </div>
    </div>
  );
}

// ────────────────────────────────────────────────────────────
// 3b. Allocation — Template picker step (returning user).
//     Shown between income and adjust. "Mois précédent" reads the
//     actual previous month name from the data.
// ────────────────────────────────────────────────────────────
function AllocationTemplatePicker({ previousMonth = 'Mai 2026' }) {
  const [choice, setChoice] = React.useState('previous');
  const monthLabel = 'Juin 2026';

  const options = [
    {
      id: 'previous',
      title: `Mois précédent (${previousMonth})`,
      desc: 'Reprendre exactement la même allocation que le mois dernier.',
      icon: 'repeat',
      meta: 'Total : 4 200 €',
    },
    {
      id: 'average',
      title: 'Moyenne 3 derniers mois',
      desc: 'Lissée sur mars, avril et mai 2026 — utile si tes mois varient.',
      icon: 'line-chart',
      meta: 'Total : 4 117 €',
    },
    {
      id: 'blank',
      title: 'Modèle vierge',
      desc: 'Repartir de zéro et tout ré-allouer manuellement.',
      icon: 'layout-grid',
      meta: 'Total : 0 €',
    },
  ];

  return (
    <div style={{ flex: 1, display: 'flex', flexDirection: 'column', minHeight: 0 }}>
      <ScreenHeader
        title={`Allouer ${monthLabel.toLowerCase()}`}
        onBack={() => {}}
        right={
          <div style={{ display: 'flex', gap: 4 }}>
            {[0, 1, 2].map(i => (
              <div key={i} style={{
                width: i === 1 ? 20 : 6, height: 6, borderRadius: 100,
                background: i <= 1 ? FOS.primary : FOS.surfaceVar,
              }} />
            ))}
          </div>
        }
      />

      <div style={{ flex: 1, minHeight: 0, overflow: 'auto', paddingBottom: 130 }}>
        <div style={{ padding: '8px 20px 0' }}>
          <div style={{ fontFamily: 'DM Sans', fontSize: 13, color: FOS.textDim, marginTop: 8 }}>
            Étape 2 sur 3
          </div>
          <div style={{
            fontFamily: 'DM Sans', fontSize: 22, fontWeight: 600,
            color: FOS.text, marginTop: 4, lineHeight: 1.3,
          }}>
            Sur quelle base partir ?
          </div>
          <div style={{
            fontFamily: 'DM Sans', fontSize: 13, color: FOS.textDim,
            marginTop: 6, lineHeight: 1.5,
          }}>
            Tu pourras ajuster chaque enveloppe à l'étape suivante.
          </div>

          <div style={{ marginTop: 18, display: 'flex', flexDirection: 'column', gap: 10 }}>
            {options.map(o => {
              const active = o.id === choice;
              return (
                <button key={o.id} onClick={() => setChoice(o.id)} style={{
                  display: 'flex', alignItems: 'flex-start', gap: 14,
                  padding: '16px 16px', borderRadius: 16,
                  background: active ? `${FOS.primary}14` : FOS.surface,
                  border: active ? `1px solid ${FOS.primary}` : `1px solid ${FOS.outline}`,
                  cursor: 'pointer', textAlign: 'left',
                }}>
                  <div style={{
                    width: 38, height: 38, borderRadius: 12,
                    background: active ? `${FOS.primary}1F` : FOS.surfaceVar,
                    color: active ? FOS.primary : FOS.textDim,
                    display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0,
                  }}><Icon name={o.icon} size={18} strokeWidth={1.7} /></div>

                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{
                      display: 'flex', justifyContent: 'space-between',
                      alignItems: 'center', gap: 8,
                    }}>
                      <span style={{
                        fontFamily: 'DM Sans', fontSize: 14, fontWeight: 600,
                        color: active ? FOS.primary : FOS.text,
                      }}>{o.title}</span>
                      {active && (
                        <div style={{
                          width: 20, height: 20, borderRadius: 100,
                          background: FOS.primary, color: FOS.onPrimary,
                          display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0,
                        }}><Icon name="check" size={12} strokeWidth={3} /></div>
                      )}
                    </div>
                    <div style={{
                      fontFamily: 'DM Sans', fontSize: 12.5, color: FOS.textDim,
                      marginTop: 4, lineHeight: 1.5,
                    }}>{o.desc}</div>
                    <div style={{
                      marginTop: 8, fontFamily: '"Geist Mono", monospace',
                      fontSize: 11, color: FOS.textDim, letterSpacing: 0.2,
                    }}>{o.meta}</div>
                  </div>
                </button>
              );
            })}
          </div>
        </div>
      </div>

      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 0,
        background: FOS.surfaceCt, borderTop: `1px solid ${FOS.outline}`,
        padding: '14px 20px 18px',
      }}>
        <button style={{
          width: '100%', padding: '16px', borderRadius: 16,
          background: FOS.primary, color: FOS.onPrimary, border: 'none',
          fontFamily: 'DM Sans', fontWeight: 700, fontSize: 15,
          cursor: 'pointer', letterSpacing: 0.3,
        }}>Continuer</button>
      </div>
    </div>
  );
}

// Inline design-note callout — visible in the prototype canvas only.
function DesignNote({ children }) {
  return (
    <div style={{
      margin: '24px 20px 0',
      padding: 12, borderRadius: 12,
      background: 'rgba(126, 184, 247, 0.06)',
      border: `1px dashed rgba(126, 184, 247, 0.25)`,
      fontFamily: 'DM Sans', fontSize: 11.5, color: FOS.textDim,
      lineHeight: 1.5,
    }}>{children}</div>
  );
}

Object.assign(window, {
  UnallocatedMonthBanner,
  HomeWithUnallocatedBanner,
  AllocationStep1NewMonth,
  AllocationTemplatePicker,
});
